public class lines {
   
   // This code determines the redundant set of any line on any divisibility
   //   plot, and provides some other information whose purpose is now lost to
   //   the passage of time.
   public static void main (String[] args) {
      int m  = util.inInt("> m =");
      int n  = util.inInt("> n =");
      int p  = util.inInt("> p =");
      int d  = util.inInt("> d =");
      int a0 = util.inInt("> a_0 =");
      int b0 = util.inInt("> b_0 =");
      int wa = util.inInt("> w_a =");
      int wb = util.inInt("> w_b =");
      
      System.out.println("d="+d+" : " +
                         "L : ("+a0+"+"+wa+"t)u+("+b0+"+"+wb+"t)v");
      
      int a0s = (int) util.mod(a0, p);
      int b0s = (int) util.mod(b0, p);
      int was = (int) util.mod(wa, p);
      int wbs = (int) util.mod(wb, p);
      
      System.out.println("d="+d+" : " +
                         "L' : ("+a0s+"+"+was+"t)u+("+b0s+"+"+wbs+"t)v");
      
      int mIn = (int) util.inv(m,p) * n;
      int inv8 = (int) util.inv(8,p);
      int ar1 = (int) util.mod(inv8 * 3, p) + p;
      int br1 = (int) util.mod(-4 * ar1 * ar1 + 3 * ar1 - mIn, p);
      int ar2 = (int) util.mod(inv8 * 1, p) + p;
      int br2 = (int) util.mod(-4 * ar2 * ar2 + 1 * ar2 - mIn, p);
      int ar3 = (int) util.mod(inv8 * -1, p) + p;
      int br3 = (int) util.mod(-4 * ar3 * ar3 - 1 * ar3 - mIn, p);
      int ar4 = (int) util.mod(inv8 * -3, p) + p;
      int br4 = (int) util.mod(-4 * ar4 * ar4 - 3 * ar4 - mIn, p);
      
      int deltaA = 0;
      int deltaB = 0;
      if (d==2) {
         deltaA = ar1-ar2;
         deltaB = br1-br2;
      } else if (d==3) {
         deltaA = ar1-ar3;
         deltaB = br1-br3;
      } else if (d==4) {
         deltaA = ar1-ar4;
         deltaB = br1-br4;
      }
      
      System.out.println("d=1 : L' : ("+
                         a0s+"+"+deltaA+"+"+was+"t)u+" + 
                         "("+b0s+"+"+deltaB+"+"+wbs+"t)v");
      
      a0s += deltaA;
      b0s += deltaB;
      
      System.out.println("d=1 : L' : ("+a0s+"+"+was+"t)u+("+b0s+"+"+wbs+"t)v");
      
      a0s = (int) util.mod(a0s, p);
      b0s = (int) util.mod(b0s, p);
      
      System.out.println("d=1 : L' : ("+a0s+"+"+was+"t)u+("+b0s+"+"+wbs+"t)v");
      
      int invwas = 1;
      while (was * invwas % p != 1) {invwas ++;}
      System.out.println("was^-1 = " + was + "^-1 = " + invwas);
      
      System.out.println("d=1 : L' : ("+
                         a0s+"+t)u+("+b0s+"+"+invwas+"*"+wbs+"t)v");
      
      wbs = invwas * wbs % p;
      
      System.out.println("d=1 : L' : ("+a0s+"+t)u+("+b0s+"+"+wbs+"t)v");
      System.out.println("d=1 : L' : ("+
                         a0s+"+(t-"+a0s+")u+("+b0s+"+"+wbs+"(t-"+a0s+"))v");
      
      b0s -= wbs * a0s;
      
      System.out.println("d=1 : L' : tu+("+b0s+"+"+wbs+"t)v");
      System.out.println("d=1 : L' : b = "+wbs+"a + "+b0s);
      
      b0s = (int) util.mod(b0s, p);
      
      if (wbs > p / 2) {
         System.out.println("ar1 = " + ar1);
         System.out.println("d=1 : L' : b = -"+wbs+"a + 2("+wbs+")("+ar1+") + "
                               + b0s);
         b0s += 2 * wbs * ar1 + b0s;
         wbs = -wbs;
         System.out.println("d=1 : L' : b = "+wbs+"a + "+b0s);
         b0s = (int) util.mod(b0s, p);
         wbs = (int) util.mod(wbs, p);
         System.out.println("d=1 : L' : b = "+wbs+"a + "+b0s);
      }
      
      System.out.println("d=1 : L' : b = "+wbs+"a + "+b0s);

      System.out.println(m + "^-1*" + n + " = " + mIn);
      System.out.println("r = ("+wbs+"-3)^2 - 16(" + b0s + ") - 16(" + mIn +
                         ") mod " + p);
      
      int r = (wbs-3) * (wbs-3) - (16 * b0s) - (16 * mIn);
      
      System.out.println("r = " + r + " mod " + p);
      
      r = (int) util.mod(r, p);
      
      System.out.println("r = " + util.mod(r, p) + " mod " + p);
      
      System.out.println("Quadratic residues modulo " + p);
      for (int i = 0; i <= p / 2; i ++) {
         System.out.println(i * i % p);
         if (r == i * i % p) {
            System.out.println("d="+d+" : " +
                               "L : ("+a0+"+"+wa+"t)u+("+b0+"+"+wb+"t)v " +
                               "is a divisible line");
         }
      }
   }
}