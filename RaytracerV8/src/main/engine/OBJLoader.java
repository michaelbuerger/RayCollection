package main.engine;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.PatternSyntaxException;

import main.math.Vec3;
import main.math.Triangle;
import main.math.MathUtils;

public class OBJLoader {
    /**
     * @param filepath path to model file
     * @return list of triangles
     */
    public static ArrayList<Triangle> loadOBJIndexed(String filepath) {
        // list of triangles, created from unsorted vertices and normals via indices
        ArrayList<Triangle> triangles = new ArrayList<Triangle>();

        // all vertices and normals
        ArrayList<Vec3> vertexPool = new ArrayList<Vec3>();
        ArrayList<Vec3> normalPool = new ArrayList<Vec3>();

        try {
            // standard stuff to read file line-by-line
            FileReader fr = new FileReader(filepath);
            BufferedReader br = new BufferedReader(fr);

            String line = ""; // holds current line
            String[] lineParts; // holds parts of line

            String[] faceParts1; // holds parts of first face specification unit
            String[] faceParts2; // holds parts of second face specification unit
            String[] faceParts3; // holds parts of third face specification unit
            int i_v1, i_vn1, i_v2, i_vn2, i_v3, i_vn3; // indices used in face spec. section
            Vec3 normal; // normal for each face spec.

            // while reader has more stuff
            while(br.ready()) {
                line = br.readLine(); // read stuff

                // process stuff
                lineParts = line.split(" ");
                // line not specifying anything useful or of wrong format
                if(lineParts.length != 4)
                    continue;

                switch(lineParts[0].trim()) {
                    case "v": // vertex
                        vertexPool.add(stringsToVec3(lineParts[1], lineParts[2], lineParts[3]));
                        break;

                    case "vn": // vertex normal
                        normalPool.add(stringsToVec3(lineParts[1], lineParts[2], lineParts[3]));
                        break;

                    case "f": // face spec.
                        // f v1/vt1/vn1 v2/vt2/vn2 v3/vt3/vn3 -->
                        // [v1, vt1, vn1]
                        // [v2, vt2, vn2]
                        // [v3, vt3, vn3]
                        faceParts1 = lineParts[1].split("/");
                        faceParts2 = lineParts[2].split("/");
                        faceParts3 = lineParts[3].split("/");

                        // something wrong with face specification format
                        if(faceParts1.length != 3 || faceParts2.length != 3 || faceParts3.length != 3) {
                            System.out.println("Formatting of face specification isn't right");
                            break;
                        }

                        // process indices
                        i_v1 = Integer.parseInt(faceParts1[0]) - 1;
                        i_vn1 = Integer.parseInt(faceParts1[2]) - 1;
                        i_v2 = Integer.parseInt(faceParts2[0]) - 1;
                        i_vn2 = Integer.parseInt(faceParts2[2]) - 1;
                        i_v3 = Integer.parseInt(faceParts3[0]) - 1;
                        i_vn3 = Integer.parseInt(faceParts3[2]) - 1;

                        // average normals of 3 points --> finds normal of plane of triangle
                        normal = MathUtils.avgVecs(new Vec3[]{ normalPool.get(i_vn1), normalPool.get(i_vn2), normalPool.get(i_vn3) });
                        
                        // add new triangle
                        triangles.add(new Triangle(vertexPool.get(i_v1), vertexPool.get(i_v2), vertexPool.get(i_v3), normal));

                        break;
                }
            }
            
            br.close();
        } catch(FileNotFoundException e) {
            System.out.println("'" + filepath + "' not found!");
        } catch(IOException e) {
            System.out.println(e);
        } catch(PatternSyntaxException e) {
            System.out.println(e);
        }

        return triangles;
    }

    private static Vec3 stringsToVec3(String s1, String s2, String s3) {
        Double n1, n2, n3;

        // parse nums
        n1 = Double.parseDouble(s1);
        n2 = Double.parseDouble(s2);
        n3 = Double.parseDouble(s3);

        return new Vec3(n1, n2, n3);
    }
}

