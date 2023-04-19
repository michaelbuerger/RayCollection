import rte_constants as const
import numpy as np
import scipy as sp

### GENERAL FLOAT/TUPLE METHODS ###

def fequals(x,y):
    return np.abs(x-y) < const.FLOATING_POINT_EPSILON

def fequals_tuple(a,b):
    if len(a) != len(b):
        return False
    for i in range(len(a)):
        if not fequals(a[i], b[i]):
            return False
    return True

def add_tuples(a,b):
    if not len(a) == len(b):
        return None

    t = list(a)
    for i in range(len(a)):
        t[i] += b[i]
    
    return tuple(t)

def sub_tuples(a,b):
    if not len(a) == len(b):
        return None

    t = list(a)
    for i in range(len(a)):
        t[i] -= b[i]
    
    return tuple(t)

def negate_tuple(a):
    t = list(a)
    for i in range(len(a)):
        t[i] = -t[i]
    
    return tuple(t)

def scalar_multiply_tuple(a, scalar):
    t = list(a)
    for i in range(len(a)):
        t[i] *= scalar
    
    return tuple(t)

def scalar_divide_tuple(a, scalar):
    t = list(a)
    for i in range(len(a)):
        t[i] /= scalar
    
    return tuple(t)

### VECTOR,POINT,COLOR METHODS ###

def point(x,y,z):
    return (x,y,z,1.0)

def vector(x,y,z):
    return (x,y,z,0.0)

def color(r,g,b):
    return (r,g,b)

# intended for v: vector
def magnitude(v):    
    return np.sqrt(v[0]**2 + v[1]**2 + v[2]**2)

# intended for v: vector
def normalize(v):
    mag = magnitude(v)
    return (v[0] / mag, v[1] / mag, v[2] / mag, 0.0)

# intended for a,b: vector
def dot(a,b):
    return a[0]*b[0] + a[1]*b[1] + a[2]*b[2] + a[3]*b[3]

# intended for a,b: vector
def cross(a,b):
    return vector(a[1]*b[2] - a[2]*b[1], a[2]*b[0] - a[0]*b[2], a[0]*b[1] - a[1]*b[0])

# intended for a,b: color
def color_multiply(a,b):
    return (a[0]*b[0], a[1]*b[1], a[2]*b[2])

### MATRIX OPERATIONS ####

def matrix2(a1,a2,b1,b2):
    return np.array([[a1,a2],[b1,b2]])

def matrix3(a1,a2,a3,b1,b2,b3,c1,c2,c3):
    return np.array([[a1,a2,a3],[b1,b2,b3],[c1,c2,c3]])

def matrix4(a1,a2,a3,a4,b1,b2,b3,b4,c1,c2,c3,c4,d1,d2,d3,d4):
    return np.array([[a1,a2,a3,a4],[b1,b2,b3,b4],[c1,c2,c3,c4],[d1,d2,d3,d4]])

def fequals_matn(a,b,n):
    if a.shape != (n, n) or b.shape != (n, n):
        return False

    for m_ in range(n):
        for n_ in range(n):
            if not fequals(a[m_][n_], b[m_][n_]):
                return False
    return True

def fequals_mat2(a,b):
    return fequals_matn(a,b,2)

def fequals_mat3(a,b):
    return fequals_matn(a,b,3)

def fequals_mat4(a,b):
    return fequals_matn(a,b,4)

def matrix_multiply(a,b):
    (_, n) = a.shape
    if b.shape[0] != n:
        raise TypeError("Attempted to multiply matrices of invalid shapes:", a.shape, b.shape)
    
    return np.dot(a,b)

def matrix_tuple_multiply(a, v):
    v_col_vector = np.array(v).transpose()
    return tuple(matrix_multiply(a, v_col_vector).transpose())

def identity2():
    return np.identity(2)

def identity3():
    return np.identity(3)

def identity4():
    return np.identity(4)

def matrix_inverse(m):
    if fequals(matrix_determinant(m), 0):
        raise TypeError("Attempted to find inverse of singular matrix, det = 0")
    return np.linalg.inv(m)

def matrix_determinant(m):
    return np.linalg.det(m)