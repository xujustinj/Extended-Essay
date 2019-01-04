# A Geometric Explanation for Prime-rich Lines on the Ulam Spiral

The revised paper can be found [here](https://drive.google.com/file/d/1Qnm2kCzFfyWKjZs3ibRenFmdy2lNG1oi/view?usp=sharing). It is identical to the [submitted version](https://drive.google.com/file/d/1oc4km03iafN8zJzZJH5y4rG1RCXt7U2-/view?usp=sharing) in content, differing only in the use of the align environment, the typeset bibliography, and the rearranged appendix.



## Foreword

### Motivation

The Extended Essay (EE) is a widely-dreaded part of the International Baccalaureate Diploma Programme, which requires students to write a 3000-4000 word paper on a topic of their choice. In short, the purpose of the undertaking is for the student to demonstrate expertise (developed through extensive individual research) on that topic, exceeding the expectations of the curriculum.

I believed that I was strong enough at mathematics that I would enjoy writing a math EE. I found the Ulam spiral from a [Numberphile video](https://youtu.be/iFuR97YcSLM), and thought it would be approachable despite lacking any formal training in the modular arithmetic that the paper came to rely so heavily on. I spent the first half of 2017 worrying about inadvertently overlapping with other papers on the subject, which I searched extensively for but never found any. I became convinced that I would be producing novel insights into a topic that had yet to be explored, because it was so obscure. In hindsight, the lack of exploration into the patterns on the Ulam spiral comes more from the fact that it is trivial to anyone with introductory training in solving quadratic Diophantine equations.

But at the time, I didn't know that, nor did I know many of the definitions and terms that would have made writing the paper much easier. I spent the second half of 2017 writing and rewriting the paper with self-defined terms, which made my EE much longer than it should be and made it a real struggle to get below the 4000 word limit.


### tl;dr: *"How can a model based on the cause of prime-rich lines on the Ulam spiral be used to produce prime numbers?"*

The paper follows a gut suspicion I had: for any number, there are certain "indivisible" lines on the spiral that contain no multiples of that number. In my EE, I take the cause of these indivisible lines to ultimately be the source of the patterns on the Ulam spiral. Then I create a model focused on locating those indivisible lines, on which one should find lots of primes. For those familiar with the topic, my approach is similar to [wheels](https://wikipedia.org/wiki/Wheel_factorization). The model in question is a plate of spaghetti code written in Java (included in this repository) that produced a majority of the graphics in my EE as well as the test data that fills its appendix.

At the beginning of my research I greatly overestimated the power that this model would have. I thought that I would be able to use indivisible lines to easily locate incredibly large primes, as long as I could find such lines efficiently. Much of my EE focuses on how symmetry can be used to turn one indivisible line into many, which indeed makes the search much easier. Nonetheless, the model is computationally demanding when large numbers come into the question.


### Remarks

For me to take on this EE topic was to reach for something much higher than my ability at the time, and it is easy to say now that my past delusions of grandeur are embarassing. But on the other hand, I am proud of my emphasis on rigour and formality as I wrote my EE, which greatly contributed to figuring out a correct (although linguistically inefficient) interpretation of what is going on with the Ulam spiral. I'm proud of giving myself a kickstart in modular arithmetic, which made part of the discrete mathematics unit in HL Math a breeze. And I'm proud of picking up using LaTeX (in TeXstudio), which certainly has since come in handy in university.

And as a cherry on top, it did receive an A.


### Acknowledgements

Thanks to my grandpa, who gave me my mathematical intuition, my teachers, for their gracious supervision and extensions, and Dr. Brady Haran, for his excellent podcasts and videos.



## Code

A program (Code/ulam_spiral.java) was developed in Java in parallel with the writing of the EE, with the following features:


### Prime Plots

The most basic feature is the option to simply render the spiral.

	> Plot type (d/m/r/s/u):
		u
	> m =
		1
	> n =
		1
	> |x|,|y| <=
		127
	> Zoom factor:
		2
	6495 primes plotted.
	Saved as Renders/Prime Plots/m1 n1, depth127 res2.png.

Saves an image of the Ulam spiral with 127 layers, where the pixels corresponding to the prime numbers (up to 65025) are filled in black. Axes passing through the origin show where quadrants start and end.

![](Code/Renders/Prime%20Plots/m1%20n1,%20depth127%20res2.png)

The model is also capable of plotting the generalized Ulam spiral as described in the paper.

	> Plot type (d/m/r/s/u):
		u
	> m =
		4
	> n =
		3
	> |x|,|y| <=
		127
	> Zoom factor:
		2
	11448 primes plotted.
	Saved as Renders/Prime Plots/m4 n3, depth127 res2.png.

Saves an image of the variant of the Ulam spiral using the numbers of the form 4n + 3.

![](Code/Renders/Prime%20Plots/m4%20n3,%20depth127%20res2.png)

On my laptop, the program was able to plot (after a few minutes) images of the Ulam spiral in excess of (180 megapixels)[Code/Renders/Prime%20Plots/m1%20n1,%20depth7000%20res1.png], which is about the largest image size that I can display with Windows Image Viewer.


### Random Plots

Random plots display the Ulam spiral if fake "primes" are chosen randomly according to the theoretical distribution of the real primes.

	> Plot type (d/m/r/s/u):
		r
	> m =
		1
	> n =
		1
	> |x|,|y| <=
		127
	> Zoom factor:
		2
	7271 "primes" plotted.
	Saved as Renders/Random Plots/m1 n1, depth127 res2.png.

![](Code/Renders/Random%20Plots/m1%20n1,%20depth127%20res2.png)

Since the patterns of diagonal lines formed by real primes on the Ulam spiral is not observed, we can deduce that some property of prime numbers contributes to the existence of the pattern.

Repeatedly using this feature with the same input will result in different plots containing similar but varying numbers of points.


### Divisibility Plots

Divisibility plots highlight on the Ulam spiral all multiples of a particular number.

	> Plot type (d/m/r/s/u):
		d
	> m =
		1
	> n =
		1
	> p =
		7
	> |x|,|y| <=
		25
	> Zoom factor:
		10
	371 points plotted.
	Saved as Renders/Divisibility Plots/m1 n1 p7, depth25 res10.png.

![](Code/Renders/Divisibility%20Plots/m1%20n1%20p7,%20depth25%20res10.png)


### Symmetry Plots

Symmetry plots highlight symmetries on the divisibility plots and use them to transform a very small number of points (which are highlighted white) into the entire divisibility plot. They were designed to verify the theoretically-predicted symmetries described in the paper.

	> Plot type (d/m/r/s/u):
		s
	> m =
		1
	> n =
		1
	> p =
		7
	> |x|,|y| <=
		25
	> Zoom factor:
		10
	The vertex of the parabola au+g(a)v is located at these points (assume tiling) in each quadrant:
	d = 1 : 10u+0v = (10,0)
	d = 2 : 8u+3v = (-3,8)
	d = 3 : 13u+3v = (-13,-3)
	d = 4 : 11u+0v = (0,-11)
	The spiral is summarized by 4 divisible points on the half-tile 10 <= x <= 13 , 0 <= y < p:
	(10,0)
	(11,3)
	(12,5)
	(13,6)
	Plotted 4 redundant sets.
	Saved as Renders/Symmetry Plots/m1 n1 p7, depth25 res10.png.

![](Code/Renders/Symmetry%20Plots/m1%20n1%20p7,%20depth25%20res10.png)

By visually confirming that the black points are in the same locations as they are in the previous divisibility plot, the symmetries are verified. The chessboard shading represents translation and rotation symmetry, while the lines represent reflection symmetry.


### Model

	> Plot type (d/m/r/s/u):
		m
	> m =
		1
	> n =
		1
	> k =
		4
	> Lines:
		250
	> Force wa=1 (y/n):
		y
	Q = 4.375
	d = 3: (131 + 1t)u + (-63 + 1t)v
	d = 2: (77 + 1t)u + (-151 + 1t)v
	d = 3: (31 + 1t)u + (-163 + 1t)v
		...
	d = 4: (178 + 1t)u + (-70 + 1t)v
	d = 4: (69 + 1t)u + (-119 + 1t)v
	d = 4: (143 + 1t)u + (-105 + 1t)v
	Primes found using model: 23013
	Primes found randomly: 6015
	R = 3.8259351620947633

Tests the model by randomly locating 250 lines which are indivisible modulo the first 4 primes. Theoretically, these lines should contain about 4.375 times more primes than lines chosen purely at random. By testing values along those lines for primes, the model finds an actual success factor of about 3.826.

Repeatedly using this feature with the same input will result in different lines and different empirical values of R.


### Criticism

As a product of two all-nighters and ignorance of good coding practice, this is easily the worst code I have ever written.

 1. Noting that the program was meant to be used only by its creator, error handling was unimplemented.
 2. There is constant typecasting between int and long types, for which I could not seem to decide on which one to use.
 3. It takes a 4000 word essay to explain how some of the lines work.
 4. I commit the ultimate sin of using "w" in variable names to represent lowercase omega.
 5. Some lines must be broken 3 times to fit in 80 characters.
 6. The comments are either missing or dastardly unhelpful.
 7. It uses inefficient algorithms whenever the efficient one is too long to write.
 8. It's nested ifs and fors and whiles all the way down.
 9. Some of the loops are non-terminating if you're not careful with your input.
10. The past me who created this had no awareness of function abstraction or object-oriented programming.
11. The style was inconsistent before I tried to clean it up, not to mention after.



## Credit

Mathematical sources are listed in the *Works Cited* section of the paper. Not listed is Wikipedia, which was an amazing source of confusion and the rare helpful remark.

Programming sources from the following URLs were used for guidance while writing the code that made the graphics in the paper possible.

- [java2s - Java Graphics How to - Create BMP format image](https://www.java2s.com/Tutorials/Java/Graphics_How_to/Image/Create_BMP_format_image.htm)
- [Oracle Java Documentation - The switch Statement](https://docs.oracle.com/javase/tutorial/java/nutsandbolts/switch.html)
- [University of Washington - Class java.awt.image.BufferedImage](https://courses.cs.washington.edu/courses/cse341/98au/java/jdk1.2beta4/docs/api/java/awt/image/BufferedImage.html)
- [Pete Kirkham on Stack Overflow - Set BufferedImage to be a color in Java](https://stackoverflow.com/a/1440781)
- [RealHowTo on Stack Overflow - How do I open an image in the default image viewer using Java on Windows?](https://stackoverflow.com/a/5825265)
