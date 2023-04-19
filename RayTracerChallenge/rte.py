import numpy as np
import scipy as sp
import PIL as pil

from rte_math import *
from rte_canvas import Canvas

if __name__ == "__main__":
    canvas = Canvas(512,512)
    for x in range(512):
        for y in range(512):
            random_color = color(np.random.random(), np.random.random(), np.random.random())
            canvas.write_pixel(x,y,random_color)
    
    canvas.create_image()
    canvas.show()

    print(matrix_inverse(identity4()))

    # CURRENTLY ON PAGE 86 of raytracer challenge textbook