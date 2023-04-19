import unittest

from rte_math import *
import rte_canvas
import numpy as np

class TestMath(unittest.TestCase):
    def test_point(self):
        p = point(1.1,2.2,3.3)
        self.assertEqual(p[0], 1.1)
        self.assertEqual(p[1], 2.2)
        self.assertEqual(p[2], 3.3)
        self.assertEqual(p[3], 1.0)

    def test_vector(self):
        p = vector(1.1,2.2,3.3)
        self.assertEqual(p[0], 1.1)
        self.assertEqual(p[1], 2.2)
        self.assertEqual(p[2], 3.3)
        self.assertEqual(p[3], 0.0)
    
    def test_color(self):
        p = color(1.1,2.2,3.3)
        self.assertEqual(p[0], 1.1)
        self.assertEqual(p[1], 2.2)
        self.assertEqual(p[2], 3.3)

        a = color(1, 0.2, 0.4)
        b = color(0.9, 1, 0.1)
        self.assertTrue(fequals_tuple(color_multiply(a, b), color(0.9, 0.2, 0.04)))
    
    def test_fequals(self):
        self.assertFalse(fequals(1.5,2.3))
        self.assertTrue(fequals(2.3,2.3))
        self.assertTrue(fequals(1.00000001,1.0))
    
    def test_fequals_tuple(self):
        self.assertFalse(fequals_tuple(vector(1,2,3), point(1,2,3)))
        self.assertTrue(fequals_tuple(vector(1,2,3), vector(1,2,3)))
        self.assertFalse(fequals_tuple(vector(1,2,4), vector(1,2,3)))
    
    def test_add_tuples(self):
        p = point(1,2,3)
        v = vector(4,5,6)

        # v + v --> still a vector
        # p + v --> now a point
        # p + p --> valid but undefined behaviour (w=2)

        self.assertTrue(fequals_tuple(add_tuples(p, v), point(5,7,9)))
        self.assertTrue(fequals_tuple(add_tuples(v, v), vector(8,10,12)))

        # let t = p + p, t is no longer a vector or a point
        t = add_tuples(p,p)
        self.assertFalse(t[3] == 0 or t[3] == 1)
    
    def test_sub_tuples(self):
        p = point(1,2,3)
        v = vector(4,5,6)

        # v - v --> vector
        # p - v --> point
        # p - p --> vector
        # v - p --> valid but undefined behaviour (w=-1)

        self.assertTrue(fequals_tuple(sub_tuples(v, v), vector(0,0,0)))
        self.assertTrue(fequals_tuple(sub_tuples(p, v), point(-3,-3,-3)))
        self.assertTrue(fequals_tuple(sub_tuples(p, p), vector(0,0,0)))

        # let t = v - p, t is no longer a vector or a point
        t = sub_tuples(v,p)
        self.assertFalse(t[3] == 0 or t[3] == 1)
    
    def test_negate_tuples(self):
        p = point(1,2,3)
        v = vector(4,5,6)

        # negating a vector is valid
        # negating a point is not valid

        self.assertTrue(fequals_tuple(negate_tuple(v), vector(-4,-5,-6)))

        # let t = -p, t is no longer a vector or a point
        t = negate_tuple(p)
        self.assertFalse(t[3] == 0 or t[3] == 1)
    
    def test_scalar_multiply_divide(self):
        a = scalar_multiply_tuple((2,4,6,8), (1/2))
        b = scalar_divide_tuple((2,4,6,8), 2)
        
        both_result = (1,2,3,4)

        self.assertEqual(a, both_result)
        self.assertEqual(b, both_result)
        self.assertEqual(a, b)
    
    def test_magnitude_normalize(self):
        v = vector(4,4,4)
        mag = magnitude(v)

        self.assertTrue(fequals(mag, np.sqrt(48)))
        self.assertTrue(fequals_tuple(normalize(v), vector(4/mag, 4/mag, 4/mag)))
    
    def test_dot_cross(self):
        a = vector(1,2,3)
        b = vector(2,3,4)
        dotprod = dot(a,b)
        crossprodab = vector(-1,2,-1)
        crossprodba = vector(1,-2,1)

        self.assertTrue(fequals(dotprod, 20))
        self.assertTrue(fequals_tuple(cross(a,b), crossprodab))
        self.assertTrue(fequals_tuple(cross(b,a), crossprodba))
    
    def test_canvas(self):
        canvas = rte_canvas.Canvas(512,512)
        canvas.write_pixel(1,2,color(1,0,0))
        canvas.write_pixel(511,511, color(0,1,0))

        self.assertEqual(canvas.pixel_at(1,2), color(1,0,0))
        self.assertEqual(canvas.pixel_at(511,511), color(0,1,0))
    
    def test_matrices(self):
        mat2 = np.array([[1,1],
                         [2,2]])
        mat3 = np.array([[1,0,0],
                         [0,1,0],
                         [0,0,1]])             
        mat4 = np.array([[1,0,0,1],
                         [0,1,0,2],
                         [0,0,1,3],
                         [0,0,1,4]])

        self.assertEqual(mat2[0][0], 1)
        self.assertEqual(mat2[1][0], 2)

        self.assertEqual(mat3[1][1], 1)
        self.assertEqual(mat4[2][3], 3)

        mat4_1 = np.array([[1,2,3,4],[5,6,7,8],[9,8,7,6],[5,4,3,2]])
        self.assertTrue(fequals_mat4(mat4_1, mat4_1))
        self.assertFalse(fequals_mat4(mat4_1, mat4))
        self.assertFalse(fequals_mat3(mat4_1, mat4_1))

        matA = np.array([[1,2,1,1],
                         [1,1,2,1],
                         [1,1,1,2],
                         [2,1,1,1]])

        matB = np.array([[1,2,3,4],
                         [5,6,7,8],
                         [1,2,3,4],
                         [5,6,7,8]])

        matAB = np.array([[17,22,27,32],
                          [13,18,23,28],
                          [17,22,27,32],
                          [13,18,23,28]])

        self.assertTrue(fequals_mat4(matrix_multiply(matA, matB), matAB))
        self.assertFalse(fequals_mat4(matrix_multiply(matB, matA), matAB))

        matD = np.array([[1,2,3,4],
                         [2,4,4,2],
                         [8,6,4,1],
                         [0,0,0,1]])
        tupleD = (1,2,3,1)
        resultD = (18,24,33,1)

        self.assertTrue(fequals_tuple(matrix_tuple_multiply(matD, tupleD), resultD))

        self.assertTrue(fequals_mat4(matD, matrix_multiply(matD, identity4())))
        self.assertTrue(fequals_mat4(matD, matrix_multiply(identity4(), matD)))

        self.assertTrue(fequals(matrix_determinant(matrix2(1,5,-3,2)), 17))

        matrixBlahBlah = matrix4(-5,2,6,-8,1,-5,1,8,7,7,-6,-7,1,-3,7,4)
        matrixBlahBlahInverse = matrix_inverse(matrixBlahBlah)
        self.assertTrue(fequals_mat4(matrixBlahBlahInverse, matrix4(0.21805,0.45113,0.24060,-0.04511,-0.80827,-1.45677,-0.44361,0.52068,-0.07895,-0.22368,-0.05263,0.19737,-0.52256,-0.81391,-0.30075,0.30639)))
        self.assertTrue(fequals_mat4(matrix_multiply(matrixBlahBlah, matrixBlahBlahInverse), identity4()))

if __name__ == '__main__':
    unittest.main()
