# Synopsis
This repository is a collection of software written by me intended to create photorealistic rendering techniques from scratch. Within the various projects, I explore and apply the following topics: raycasting, raytracing, photorealistic rendering, concurrent/multi-threaded programming, and GPU parallelization.

To take a look at the various renders I've produced, check out renders directories within each Raytracer file e.g. 'Raytracer*/renders/'.

## The Journey
### Raytracer v1
In this iteration. I simply tried rendering spheres as they can easily be described by a position and a radius. Given the formula of a sphere, the program inefficiently "walks" a grid of rays through the world till they either reach a maximum distance or end up inside of the sphere. Once a hit point is discovered, a normal vector can easily be calculated and simple directional lighting calculations are performed.

### Raytracer v2
In this iteration, a grid of rays is cast out towards a loaded model and collision calculations are performed on each triangle of the mesh. Given this, pixel by pixel rendering and simple lighting calculations can be performed on vary complicated models. However, it faces problems with inefficient rendering methods that drastically decrease in performance as resolution and triangle counts are increased. Due to the method by which triangle collisions are calculated, there are slight errors in the final product which manifest themselves as slightly overlapping edges and small "holes" in the produced image.

### Raytracer v4
In this iteration, almost the same methods are used as before. However, the code is entirely refactored, better organized, and especially better performing as I implemented multi-threading functionality. This showed reduction in render times with extremely complicated models (50,000+ triangles) and high-pixel counts (Over 4 million). In some cases, render times were reduced to as low as 20% as compared to the previous iterations. Ideally, given the optimal concurrent implementation, this would be more like 6%, but its still a massive improvement. Furthermore, it may not be worth the time to further optimize the multi-threading approach and instead begin exploring GPU parallelization of the whole process.

