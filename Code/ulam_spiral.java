import java.awt.Color;
import java.awt.Desktop;
import java.awt.Graphics2D;

import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;

import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;


public class ulam_spiral {
   
   // Colour constants.
   public static Color axis  = new Color(191, 191, 191);
   public static Color line  = new Color(127, 127, 127);
   public static Color tile1 = new Color(191, 255, 191);
   public static Color tile2 = new Color(191, 191, 255);
   
   
   public static void main (String[] args) {
      System.out.println("d: divisibility plot modulo p");
      System.out.println("m: the model itself");
      System.out.println("r: probability of the pixel being coloured black " +
                         "is 1/ln(f_{m,n}(P))-1.08366\\)");
      System.out.println("s: divisibility plot modulo p, " +
                         "generated using symmetry");
      System.out.println("u: prime points are coloured black");
      switch (util.inChar("> Plot type (d/m/r/s/u):")) {
         case 'd': d(); break;
         case 'm': m(); break;
         case 'r': r(); break;
         case 's': s(); break;
         case 'u': u(); break;
      }
   }
   
   
   // f(m,n,d,a,b) produces the value of f_{m,n}(au+bv) which is defined in the
   //   paper to equal (4a^2 + 2dab - 5ab)m + n.
   static long f (int m, int n, int d, long a, long b) {
      return (4 * a * a + (2 * d - 5) * a + b) * m + n;
   }
   
   
   // isValid(d,a,b) produces true if (au + bv) is a valid coordinate in the
   //   quadrant numbered d, or false otherwise.
   static boolean isValid (int d, long a, long b) {
      
      // 1. The origin is an ambiguous point that is part of all quadrants.
      if ((a == 0) && (b == 0)) {
         return true;
      }
      
      // 2. Other than at the origin, the boundary conditions differ between the
      //    quadrants.
      switch (d) {
         case 1:  return ((-a + 2 <= b) && (b <= a));
         case 4:  return ((-a + 1 <= b) && (b <= a + 1));
         default: return ((-a <= -b) && (-b <= a - 1));
      }
   }
   
   
   static void d () {
      int m     = util.inInt("> m =");
      int n     = util.inInt("> n =");
      int p     = util.inInt("> p =");
      int depth = util.inInt("> |x|,|y| <=");
      int res   = util.inInt("> Zoom factor:");
      int size  = 2 * depth + 1;
      
      BufferedImage img = new BufferedImage(size, size,
                                            BufferedImage.TYPE_USHORT_GRAY);
      img = axes(img, depth);
      img = plotD(img, p, depth, m, n);
      img = stretch(img, size * res, false);
      savePNG (img, "Renders/Divisibility Plots/m" + m + " n" + n + " p" + p +
                  ", depth" + depth + " res" + res + ".png");
   }
   
   
   static void r () {
      int m     = util.inInt("> m =");
      int n     = util.inInt("> n =");
      int depth = util.inInt("> |x|,|y| <=");
      int res   = util.inInt("> Zoom factor:");
      int size  = 2 * depth + 1;
      
      BufferedImage img = new BufferedImage(size, size,
                                            BufferedImage.TYPE_USHORT_GRAY);
      img = axes(img, depth);
      img = plotR(img, depth, m, n);
      img = stretch(img, size * res, false);
      savePNG (img, "Renders/Random Plots/m" + m + " n" + n + ", depth" + depth
                  + " res" + res + ".png");
   }
   
      
   // The goal of the model is to produce arbitrarily many lines indivisible by
   //   the first k primes.
   // The step numbers are in accordance with how the algorithm is described in
   //   section 6.1 of the paper itself.
   static void m () {
      
      // 0. Gather the parameters.
      int m = util.inInt("> m =");
      int n = util.inInt("> n =");
      int k = util.inInt("> k =");
      int lines = util.inInt("> Lines:");
      boolean simple = (util.inChar("> Force wa=1 (y/n):") == 'y');
      
      // 1. Produce the list of the first p primes and determine the expected Q
      //    value.
      int[] p = util.primes(k);
      double q = 1;
      for (int i = 0; i < k; i ++) {
         q *= ((double) p[i]) / ((double) p[i] - 1);
      }
      System.out.println("Q = " + q);
      
      // 2. If m and n are not coprime, then return, since f will almost never
      //    be prime. Otherwise, if m is divisible by some p[i], but n isn't,
      //    then f is never divisible by p[i]. Then, all lines are indivisible
      //    modulo p[i], so this p[i] can be ignored. It is set to 0. The
      //    variable named coprimes tracks the number of primes in p for which
      //    this isn't the case; this is important for later array management.
      int coprimes = k;
      for (int i = k - 1; i >= 0; i --) {
         if (m % p[i] == 0) {
            if (n % p[i] == 0) {
               System.out.println(m + " and " + n + " are both multiples of " +
                                  p[i] + ", there are no prime-rich lines.");
               return;
            }
            p[i] = 0;
            coprimes --;
         }
      }
      
      // 3.
      
      // a. Calculate the multiplicative inverses of important constants to save
      //    having to calculate them multiple times.
      int[] mIn   = new int[k]; // mIn[i] * m = n mod p[i]
      int[] inv8  = new int[k]; // inv8[i] * 8 = 1 mod p[i]
      int[] inv16 = new int[k]; // inv16[i] * 16 = 1 mod p[i]
      for (int i = 0; i < k; i ++) {
         if (p[i] > 0) {
            mIn[i] = (int) util.inv(m, p[i]) * n % p[i];
            inv8[i] = (int) util.inv(8, p[i]);
            inv16[i] = (int) util.inv(16, p[i]);
         }
      }
      
      // b. Calculate the values of delta.
      int deltaA1[][] = new int[5][k]; int deltaB1[][] = new int[5][k]; // d-2 k
      for (int d = 1; d < 5; d ++) {
         for (int i = 0; i < k; i ++) {
            if (p[i] > 0) {
               deltaA1[d][i] = (int) util.mod((inv8[i] * (5 - 2 * d)) -
                                              (inv8[i] * (5 - 2 * 1)), p[i]);
               deltaB1[d][i] = (int) util.mod((inv8[i] * (1 - 4 * inv8[i]) *
                                               (5 - 2 * d) * (5 - 2 * d)) -
                                              (inv8[i] * (1 - 4 * inv8[i]) *
                                               (5 - 2 * 1) * (5 - 2 * 1)),
                                              p[i]);
            }
         }
      }
      
      // Store the values of c in a 3-D array. Not every position in the array
      //   is used.
      int[][][] c = new int[k][p[k-1]/2][p[k-1]];
      for (int i = 1; i < k; i ++) {
         if (p[i] > 0) {
            
      // 4. Prepare a list of nonresidues modulo each prime.
            int[] nonresidue = util.nonresidues(p[i]);

      // 5. Calculate c[i][j][s] to be the value of c when considering the jth
      //    nonresidue modulo the ith prime and a line of slope s.
            for (int j = 0; j * 2 < p[i] - 1; j ++) {
               for (int s = 1; s < p[i]; s ++) {
                  c[i][j][s] = (int) util.mod(-inv16[i] * 9 * s + s * mIn[i] +
                                              inv8[i] * 3 + (nonresidue[j] - 1)
                                                 * inv16[i] * util.inv(s,p[i]),
                                              p[i]);
               }
            }
         }
      }
      
      // Randomly generate arbitrarily many indivisible lines.
      long product = 1;
      int pTotal = 0;
      for (int l = lines; l > 0; l --) {
         
      // 6. For each prime coprime with m, select a random statement about that
      //    prime.
         int[] select = new int[coprimes];
         int index = 0;
         for (int i = 0; i < k; i ++) {
            if (p[i] > 0) {
               select[index] = i; index ++;
            }
         }
         int[] pSelect = new int[coprimes];
         int[] sSelect = new int[coprimes];
         int[] cSelect = new int[coprimes];
         for (int i = 0; i < coprimes; i ++) {
            pSelect[i] = p[select[i]];
            if (simple) {
               sSelect[i] = 1;
            } else {
               sSelect[i] = (int) util.rand(1,pSelect[i]-1);
            }
            cSelect[i] = c[select[i]]
               [(int) util.rand(0,(pSelect[i]-3)/2)][sSelect[i]];
         }
         if (p[0] == 2) {
            sSelect[0] = 1;
            cSelect[0] = (1 + n) % 2;
         }
          
      // 7. Select a random quadrant.
         int d = (int) util.rand(1, 4);
         for (int i = 0; i < coprimes; i ++) {
            cSelect[i] = (int) util.mod(cSelect[i]
                                           - sSelect[i] * deltaB1[d][select[i]]
                                           + deltaA1[d][select[i]], pSelect[i]);
         }
         
      // 8. Random parameters are chosen for the indivisible lines to be
      //    generated.
         int[] a0 = new int[coprimes];
         int[] b0 = new int[coprimes];
         int[] wa = new int[coprimes];
         int[] wb = new int[coprimes];
         for (int i = 0; i < coprimes; i ++) {
            b0[i] = (int) util.rand(0, pSelect[i]-1);
            if (simple) {
               wb[i] = 1;
            } else {
               wb[i] = (int) util.rand(1, pSelect[i]-1);
            }
            a0[i] = (sSelect[i] * b0[i] + cSelect[i]) % pSelect[i];
            wa[i] = (sSelect[i] * wb[i]) % pSelect[i];
         }
         
      // 9. The Chinese Remainder Theorem is used to determine the remaining
      //    parameters of the indivisible lines. The parameters are reduced to
      //    the least absolute value or the least positive value.
         product = util.product(pSelect);
         long waprime = util.crt(wa, pSelect);
         if (waprime < 0) {
            waprime += product;
         }
         long wbprime = util.crt(wb, pSelect);
         if (wbprime < -product / 2) {
            wbprime += product;
         } else if (wbprime > product / 2) {
            wbprime -= product;
         }
         long a0prime = util.crt(a0, pSelect);
         if (a0prime < 0) {
            a0prime += product;
         }
         long b0prime = util.crt(b0, pSelect);
         if (b0prime < -product / 2) {
            b0prime += product;
         } else if (b0prime > product / 2) {
            b0prime -= product;
         }
         if ((simple) && (b0prime > 0)) {b0prime -= product;}
         if (!(((0 <= wbprime) && (wbprime <= waprime)) ||
               ((0 >= wbprime) && (wbprime >= waprime)))) {
            waprime -= product;
            wbprime -= product;
         }
         
      // 10. Output the line.
         System.out.println("d = " + d + ": (" + a0prime + " + " + waprime +
                            "t)u + (" + b0prime + " + " + wbprime + "t)v");
         
      // The following evaluation test is described in section 6.3 of the paper
      //   itself.
         if (simple) {
            long t = (- a0prime - b0prime) / 2 - 2;
            int pCount = 0;
            int points = 250;
            while (points > 0) {
               long a = a0prime + waprime * t;
               long b = b0prime + wbprime * t;
               if (isValid(d, a, b)) {
                  points --;
                  long f = f(m, n, d, a, b);
                  if (util.isPrime(f)) {
                     pCount ++;
                  }
               }
               t ++;
            }
            pTotal += pCount;
         }
      }
      
      // Output the results of the evaluation, and generate 250 random points
      //   from random lines to compare.
      if (simple) {
         System.out.println("Primes found using model: " + pTotal);
         int pFakeTotal = 0;
         for (int i = 0; i < lines; i ++) {
            int d = (int) util.rand(1, 4);
            long a0fake = util.rand(0, product);
            long b0fake = util.rand(-product, 0);
            long t = (- a0fake - b0fake) / 2 - 2;
            int pFakeCount = 0;
            int points = 250;
            while (points > 0) {
               long a = a0fake + t;
               long b = b0fake + t;
               if (isValid(d,a,b)) {
                  points --;
                  long f = f(m, n, d, a, b);
                  if (util.isPrime(f)) {pFakeCount ++;}
               }
               t ++;
            }
            pFakeTotal += pFakeCount;
         }
         System.out.println("Primes found randomly: " + pFakeTotal);
         System.out.println("R = " + ((double) pTotal) / ((double) pFakeTotal));
      }
   }
   
   
   // requires: p is odd and coprime to m
   static void s () {
      int m     = util.inInt("> m =");
      int n     = util.inInt("> n =");
      int p     = util.inInt("> p =");
      int depth = util.inInt("> |x|,|y| <=");
      int res   = util.inInt("> Zoom factor:");
      int size  = 2 * depth + 1;
      
      // This is terribly inefficient considering that the inv function was
      //   written after writing this code; replacing these lines with the inv
      //   function was forgotten.
      int mIn = n;
      while (m * mIn % p != n % p) {
         mIn += n;
      }
      int inv8 = 1;
      while (8 * inv8 % p != 1) {
         inv8 += 1;
      }
      
      int ar1 = (int) util.mod(inv8 * 3, p) + p;
      int br1 = (int) util.mod(inv8 * (1 - p) / 2 * 9 - mIn, p);
      int ar2 = (int) util.mod(inv8 * 1, p) + p;
      int br2 = (int) util.mod(inv8 * (1 - p) / 2 * 1 - mIn, p);
      int ar3 = (int) util.mod(inv8 * -1, p) + p;
      int br3 = (int) util.mod(inv8 * (1 - p) / 2 * 1 - mIn, p);
      int ar4 = (int) util.mod(inv8 * -3, p) + p;
      int br4 = (int) util.mod(inv8 * (1 - p) / 2 * 9 - mIn, p);
      
      System.out.println("The vertex of the parabola au+g(a)v is located at " +
                         "these points (assume tiling) in each quadrant:");
      System.out.println("d = 1 : " + ar1 + "u+" + br1 +
                         "v = (" + ar1 + "," + br1 + ")");
      System.out.println("d = 2 : " + ar2 + "u+" + br2 +
                         "v = (" + -br2 + "," + ar2 + ")");
      System.out.println("d = 3 : " + ar3 + "u+" + br3 +
                         "v = (" + -ar3 + "," + -br3 + ")");
      System.out.println("d = 4 : " + ar4 + "u+" + br4 +
                         "v = (" + br4 + "," + -ar4 + ")");
      
      System.out.println("The spiral is summarized by " + (p+1)/2 +
                         " divisible points on the half-tile " +
                         ar1 + " <= x <= " + (ar1 + p/2) + " , 0 <= y < p:");
      int[] a1 = new int[(p + 1) / 2];
      int[] b1 = new int[(p + 1) / 2];
      a1[0] = ar1;
      b1[0] = br1;
      System.out.println("(" + a1[0] + "," + b1[0] + ")");
      for (int i = 1; i <= p / 2; i ++) {
         a1[i] = i + ar1;
         b1[i] = (int) util.mod(-4 * a1[i] * a1[i] + 3 * a1[i] - mIn, p);
         System.out.println("(" + a1[i] + "," + b1[i] + ")");
      }

      BufferedImage img = new BufferedImage(size, size,
                                            BufferedImage.TYPE_USHORT_555_RGB);
      img = tiles(img, p, depth, ar1, br1, ar2, br2, ar3, br3, ar4, br4);
      img = plotS(img, p, depth, ar1, br1, ar2, br2, ar3, br3, ar4, br4,
                  a1, b1, n);
      img = stretch(img, size * res, true);
      img = drawS(img, p, depth, res, ar1, ar2, ar3, ar4);
      Graphics2D graphics = img.createGraphics();
      graphics.setPaint(Color.WHITE);
      for (int i = 0; i < a1.length; i ++) {
         graphics.fillRect((depth + a1[i]) * res, (depth - b1[i]) * res,
                           res, res);
      }
      savePNG (img, "Renders/Symmetry Plots/m" + m + " n" + n + " p" + p +
               ", depth" + depth + " res" + res + ".png");
   }
   
   
   static void u () {
      int m     = util.inInt("> m =");
      int n     = util.inInt("> n =");
      int depth = util.inInt("> |x|,|y| <=");
      int res   = util.inInt("> Zoom factor:");
      int size  = 2 * depth + 1;
      
      BufferedImage img = new BufferedImage(size, size,
                                            BufferedImage.TYPE_USHORT_GRAY);
      img = axes(img, depth);
      img = plotU(img, depth, m, n);
      img = stretch(img, size * res, false);
      savePNG (img, "Renders/Prime Plots/m" + m + " n" + n + ", depth" + depth +
               " res" + res + ".png");
   }
   
   static BufferedImage axes (BufferedImage img, int depth) {
      Graphics2D graphics = img.createGraphics();
      graphics.setPaint(Color.WHITE);
      graphics.fillRect(0, 0, img.getWidth(), img.getHeight());
      graphics.setPaint(axis);
      graphics.drawLine(0, depth, img.getWidth(), depth);
      graphics.drawLine(depth, 0, depth, img.getHeight());
      graphics.drawLine(0, 0, img.getWidth()-1, img.getHeight()-1);
      graphics.drawLine(0, img.getHeight()-1, img.getWidth()-1, 0);
      return img;
   }
   
   
   static BufferedImage plotD (BufferedImage img, int p, int depth,
                               int m, int n) {
      int x = 0;
      int y = 0;
      int points = 0;   
      int max = n + m * img.getWidth() * img.getHeight();
      for (int i = n; i < max; i += m) {
         if (i % p == 0) {
            img.setRGB(depth + x, depth - y, Color.BLACK.getRGB());
            points ++;
         }
         if (y > -x) {
            if (y < x) {
               y ++;
            } else {
               x --;
            }
         } else {
            if (y > x) {
               y --;
            } else {
               x ++;
            }
         }
      }      
      System.out.println(points + " points plotted.");
      return img;
   }
   
   
   static BufferedImage plotR (BufferedImage img, int depth, int m, int n) {
      int x = 0;
      int y = 0;
      int points = 0;
      int max = n + m * img.getWidth() * img.getHeight();
      for (int i = n; i < max; i += m) {
         if (Math.random() < 1.0 / (Math.log(i) - 1.08366)) {
            img.setRGB(depth + x, depth - y, Color.BLACK.getRGB());
            points ++;
         }
         if (y > -x) {
            if (y < x) {
               y ++;
            } else {
               x --;
            }
         } else {
            if (y > x) {
               y --;
            } else {
               x ++;
            }
         }
      }
      System.out.println(points + " \"primes\" plotted.");
      return img;
   }
   
   
   static BufferedImage plotS (BufferedImage img, int p, int depth,
                               int ar1, int br1, int ar2, int br2,
                               int ar3, int br3, int ar4, int br4,
                               int a[], int b[], int n) {
      
      // a[i]u+b[i]v is point #i from the summary
      for (int i = 0; i < a.length; i ++) {
         int a1 = a[i] % p;
         int a2 = (a[i] + ar2 - ar1 + p) % p;
         int a3 = (a[i] + ar3 - ar1 + p) % p;
         int a4 = (a[i] + ar4 - ar1 + p) % p;
         int b1 = b[i];
         int b2 = p - (b[i] + br2 - br1 + p) % p;
         int b3 = p - (b[i] + br3 - br1 + p) % p;
         int b4 = (b[i] + br4 - br1 + p) % p;
         
         for (int y = (depth % p < b1) ?
                 depth / p * p - p + b1 :
                 depth / p * p + b1;
              y >= -depth; y -= p) { // d = 1
            for (int x = (depth % p < a1) ?
                    depth / p * p - p + a1 :
                    depth / p * p + a1;
                 ((-x + 2 <= y) && (y <= x)); x -= p)
               img.setRGB(depth + x, depth - y, Color.BLACK.getRGB());
            if (i > 0) {
               a1 = (2 * ar1 - a1 + p) % p;
               for (int x = (depth % p < a1) ?
                       depth / p * p - p + a1 :
                       depth / p * p + a1;
                    ((-x + 2 <= y) && (y <= x)); x -= p)
                  img.setRGB(depth + x, depth - y, Color.BLACK.getRGB());
            }
         } for (int x = (depth % p < b2) ?
                   depth / p * p - p + b2 :
                   depth / p * p + b2;
                x >= -depth; x -= p) { // d = 2
            for (int y = (depth % p < a2) ?
                    depth / p * p - p + a2 :
                    depth / p * p + a2;
                 ((-y <= x) && (x <= y - 1)); y -= p)
               img.setRGB(depth + x, depth - y, Color.BLACK.getRGB());
            if (i > 0) {
               a2 = (2 * ar2 - a2 + p) % p;
               for (int y = (depth % p < a2) ?
                       depth / p * p - p + a2 :
                       depth / p * p + a2;
                    ((-y <= x) && (x <= y - 1)); y -= p)
                  img.setRGB(depth + x, depth - y, Color.BLACK.getRGB());
            }
         } for (int y = (depth % p < b3) ?
                   depth / p * p - p + b3 :
                   depth / p * p + b3;
                y >= -depth; y -= p) { // d = 3
            for (int x = -((depth % p < a3) ?
                              depth / p * p - p + a3 :
                              depth / p * p + a3);
                 ((x <= y) && (y <= -x - 1)); x += p)
               img.setRGB(depth + x, depth - y, Color.BLACK.getRGB());
            if (i > 0) {
               a3 = (2 * ar3 - a3 + p) % p;
               for (int x = -((depth % p < a3) ?
                                 depth / p * p - p + a3 :
                                 depth / p * p + a3);
                    ((x <= y) && (y <= -x - 1)); x += p)
                  img.setRGB(depth + x, depth - y, Color.BLACK.getRGB());
            }
         } for (int x = (depth % p < b4) ?
                   depth / p * p - p + b4 :
                   depth / p * p + b4;
                x >= -depth; x -= p) { // d = 4
            for (int y = -((depth % p < a4) ?
                              depth / p * p - p + a4 :
                              depth / p * p + a4);
                 ((y + 1 <= x) && (x <= -y + 1)); y += p)
               img.setRGB(depth + x, depth - y, Color.BLACK.getRGB());
            if (i > 0) {
               a4 = (2 * ar4 - a4 + p) % p;
               for (int y = -((depth % p < a4) ?
                                 depth / p * p - p + a4 :
                                 depth / p * p + a4);
                    ((y + 1 <= x) && (x <= -y + 1)); y += p)
                  img.setRGB(depth + x, depth - y, Color.BLACK.getRGB());
            }
         }
      }
      if (n % p == 0) {
         img.setRGB(depth, depth, Color.BLACK.getRGB());
      }
      System.out.println("Plotted " + (p+1)/2 + " redundant sets.");
      return img;
   }
   
   
   static BufferedImage plotU (BufferedImage img, int depth, int m, int n) {
      int x = 0;
      int y = 0;
      int points = 0;
      int max = n + m * img.getWidth() * img.getHeight();
      boolean[] prime = util.eratosthenes(max);
      for (int i = n; i < max; i += m) {
         if (prime[i]) {
            img.setRGB(depth + x, depth - y, Color.BLACK.getRGB());
            points ++;
         }
         if (y > -x) {
            if (y < x) {
               y ++;
            } else {
               x --;
            }
         } else {
            if (y > x) {
               y --;
            } else {
               x ++;
            }
         }
      }
      System.out.println(points + " primes plotted.");
      return img;
   }
   
   
   static BufferedImage tiles (BufferedImage img, int p, int depth,
                               int ar1, int br1, int ar2, int br2,
                               int ar3, int br3, int ar4, int br4) {
      int a1 = ar1 - (p - 1) / 2;
      int a2 = ar2 - (p - 1) / 2;
      int a3 = ar3 - (p - 1) / 2;
      int a4 = ar4 - (p - 1) / 2;
      int b2 = br2 - br1;
      int b3 = br3 - br1;
      int b4 = br4 - br1;
      for (int x = -depth; x <= depth; x ++) {
         for (int y = -depth; y <= depth; y ++) {
            if ((-x + 2 <= y) && (y <= x))
               img.setRGB(depth + x, depth - y,
                          (((Math.floor(((x - a1) / (double) p) +
                                        Math.floor(y / (double) p)))
                               % 2 != 0) ? tile1 : tile2).getRGB());
            else if ((- y <= x) && (x <= y - 1))
               img.setRGB(depth + x, depth - y,
                          (((Math.floor(((y - a2) / (double) p) +
                                        Math.floor((-x - b2) / (double) p)))
                               % 2 != 0) ? tile1 : tile2).getRGB());
            else if (x <= y)
               img.setRGB(depth + x, depth - y,
                          (((Math.floor(((-x - a3) / (double) p) +
                                        Math.floor((-y - b3) / (double) p)))
                               % 2 != 0) ? tile1 : tile2).getRGB());
            else
               img.setRGB(depth + x, depth - y,
                          (((Math.floor(((-y - a4) / (double) p) +
                                        Math.floor((x - b4) / (double) p)))
                               % 2 != 0) ? tile1 : tile2).getRGB());
         }
      }
      Graphics2D graphics = img.createGraphics();
      graphics.setPaint(axis);
      graphics.drawLine(0, depth, img.getWidth(), depth);
      graphics.drawLine(depth, 0, depth, img.getHeight());
      return img;
   }
   
   
   static BufferedImage stretch (BufferedImage img, int size, boolean colour) {
      BufferedImage stretched = (colour) ?
         new BufferedImage(size, size, BufferedImage.TYPE_USHORT_555_RGB) :
         new BufferedImage(size, size, BufferedImage.TYPE_USHORT_GRAY);
      Graphics2D graphics = stretched.createGraphics();
      graphics.drawImage(img, 0, 0, size, size, null);
      return stretched;
   }
   
   
   static BufferedImage drawS (BufferedImage img, int p, int depth, int res,
                               int ar1, int ar2, int ar3, int ar4) {
      Graphics2D graphics = img.createGraphics();
      graphics.setPaint(line);
      if (res % 2 == 0) {
         for (int x = ar1 % p; x <= depth; x += p) {
            for (int y = -x + 2; y <= x; y ++) {
               if (img.getRGB(((depth + x) * res), (depth - y) * res)
                      != Color.BLACK.getRGB())
                  graphics.fillRect((depth + x) * res + res / 2 - 1,
                                    (depth - y) * res, 2, res);
            }
         } for (int y = ar2 % p; y <= depth; y += p) {
            for (int x = -y; x <= y - 1; x ++) {
               if (img.getRGB(((depth + x) * res), (depth - y) * res)
                      != Color.BLACK.getRGB())
                  graphics.fillRect((depth + x) * res,
                                    (depth - y) * res + res / 2 - 1, res, 2);
            }
         } for (int x = -ar3 % p; x >= -depth; x -= p) {
            for (int y = x; y <= -x - 1; y ++) {
               if (img.getRGB(((depth + x) * res), (depth - y) * res)
                      != Color.BLACK.getRGB())
                  graphics.fillRect((depth + x) * res + res / 2 - 1,
                                    (depth - y) * res, 2, res);
            }
         } for (int y = -ar4 % p; y >= -depth; y -= p) {
            int max = (-y + 1 > depth) ? depth : -y + 1;
            for (int x = y + 1; x <= max; x ++) {
               if (img.getRGB(((depth + x) * res), (depth - y) * res)
                      != Color.BLACK.getRGB())
                  graphics.fillRect((depth + x) * res,
                                    (depth - y) * res + res / 2 - 1, res, 2);
            }
         }
      } else {
         for (int x = ar1 % p; x <= depth; x += p) {
            for (int y = -x + 2; y <= x; y ++) {
               if (img.getRGB(((depth + x) * res), (depth - y) * res)
                      != Color.BLACK.getRGB())
                  graphics.fillRect((depth + x) * res + res / 2,
                                    (depth - y) * res, 1, res);
            }
         } for (int y = ar2 % p; y <= depth; y += p) {
            for (int x = -y; x <= y - 1; x ++) {
               if (img.getRGB(((depth + x) * res), (depth - y) * res)
                      != Color.BLACK.getRGB())
                  graphics.fillRect((depth + x) * res,
                                    (depth - y) * res + res / 2, res, 1);
            }
         } for (int x = -ar3 % p; x >= -depth; x -= p) {
            for (int y = x; y <= -x - 1; y ++) {
               if (img.getRGB(((depth + x) * res), (depth - y) * res)
                      != Color.BLACK.getRGB())
                  graphics.fillRect((depth + x) * res + res / 2,
                                    (depth - y) * res, 1, res);
            }
         } for (int y = -ar4 % p; y >= -depth; y -= p) {
            int max = (-y + 1 > depth) ? depth : -y + 1;
            for (int x = y + 1; x <= max; x ++) {
               if (img.getRGB(((depth + x) * res), (depth - y) * res)
                      != Color.BLACK.getRGB())
                  graphics.fillRect((depth + x) * res,
                                    (depth - y) * res + res / 2, res, 1);
            }
         }
      }      
      return img;
   }
   
   
   static void savePNG (BufferedImage img, String path) {
      try {
         RenderedImage render = img;
         ImageIO.write(render, "png", new File(path));
         System.out.println("Saved as " + path + ".");
         Desktop.getDesktop().open(new File(path));
      } catch (IOException e) {e.printStackTrace();}
   }
}