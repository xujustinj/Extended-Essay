import java.util.Scanner;

import java.util.Arrays;


public class util {
   
   
   // For obtaining user inputs.
   public static Scanner scanner = new Scanner(System.in);
   public static String inStr (String prompt) {
      System.out.print(prompt);
      return scanner.nextLine();
   }
   public static int inInt (String prompt) {
      return Integer.parseInt(inStr(prompt));
   }
   public static int inChar (String prompt) {
      return inStr(prompt).charAt(0);
   }
   
   
   // rand(a,b) produces a random long chosen from the uniform probability
   //   distribution on [a,b].
   // requires: a < b
   public static long rand (long a, long b) {
      return a + (long) (Math.random() * (b - a + 1));
   }
   
   
   // mod(a,p) produces the long b in [0,p) such that a = b mod p.
   // requires: p > 0
   public static long mod (long a, long p) {
      long b = a % p;
      return b < 0 ? b + p : b;
   }
   
   
   // eratosthenes(n) produces an array with indices in [0,n), where
   //   array[i] is true if i is prime, or false otherwise.
   // requires: max >= 0
   public static boolean[] eratosthenes (int n) {
      
      // 1. Begin with an array filled with true (all numbers are assumed to be
      //    prime).
      boolean[] isPrime = new boolean[n];
      Arrays.fill(isPrime, true);
      
      // 2. Initialize 0 and 1 to be non-prime.
      isPrime[0] = isPrime[1] = false;
      
      // 3. Execute the algorithm of the sieve of Eratosthenes on the remaining
      //    numbers.
      int max_i = (int) Math.sqrt(n);
      for (int i = 2; i <= max_i; i ++) {
         if (isPrime[i]) {
            for (int j = i * i; j < n; j += i) {
               isPrime[j] = false;
            }
         }
      }
      
      // 4. Return the array.
      return isPrime;
   }
   
   
   // primes(n) produces an array of the first n primes, sorted by increasing
   //   value.
   // requires: n > 1
   // This code is employs an inefficient algorithm, which is okay since this
   //   function is designed for values of n below 10.
   static int[] primes (int n) {
      
      // 1. Declare the array of primes and initialize its first few values.
      int[] prime = new int[n];
      prime[0] = 2;
      prime[1] = 3;
      
      // 2. For the remainder of the positions in primes:
      for (int i = 2; i < n; i ++) {
         
         // a. Beginning at the previous prime plus 2, the candidate is the next
         //    number that is being considered to be included in prime.
         int candidate = prime[i - 1] + 2;
         
         // b. If the candidate is found to be divisible by any of the previous
         //    primes, then it is not prime, and the candidate is
         //    incremented by 2 before repeating this step...
         boolean isPrime = false;
         while (!isPrime) {
            isPrime = true;
            for (int j = 0; j < i; j ++) {
               if (candidate % prime[j] == 0) {
                  isPrime = false;
                  candidate += 2;
                  break;
               }
            }
         }
         
         // c. ... otherwise, the candidate is prime, and it is included in
         //    prime.
         prime[i] = candidate;
      }
      
      // 3. Once full, prime is returned.
      return prime;
   }
   
   
   // isPrime(n) produces true if n is prime, or false otherwise.
   // This code employs an inefficient algorithm.
   static boolean isPrime (long n) {
      
      // A. All numbers less than 2 are not prime.
      if (n < 2) {
         return false;
      }
      
      // B. 2 is prime.
      if (n == 2) {
         return true;
      }
      
      // C. All even numbers other than 2 are not prime.
      if (n % 2 == 0) {
         return false;
      }
      
      // D. Assuming n is odd, all factors of it are odd. Finding an odd factor
      //    of n in [3, sqrt(n)] would prove n to not be prime...
      int max_i = (int) Math.sqrt(n);
      for (long i = 3; i <= max_i; i += 2) {
         if (n % i == 0) {
            return false;
         }
      }
      
      // E. ... and not finding such a factor would prove n to be a prime
      //    number.
      return true;
   }
   
   
   // product(list) produces the product of all numbers in list.
   // requires: list is nonempty
   static long product (int[] list) {
      long p = list[0];
      for (int i = 1; i < list.length; i ++) {
         p *= list[i];
      }
      return p;
   }
   
   
   // eea(a,b) produces (using the Extended Euclidean Algorithm) an array pair
   //   of integer solutions {x,y} to the equation ax + by = gcd(a,b).
   static long[] eea (long a, long b) {
      long x; long x1 = 0; long x2 = 1;
      long y; long y1 = 1; long y2 = 0;
      long q; long r;
      while (b > 0) {
         q = a / b;
         r = a - q * b;
         x = x2 - q * x1;
         y = y2 - q * y1;
         a = b;
         b = r;
         x2 = x1;
         x1 = x;
         y2 = y1;
         y1 = y;
      }
      return new long[] {x2, y2};
   }
   
   
   // inv(a,p) produces (using the Extended Euclidean Algorithm) the
   //   multiplicative inverse of a modulo p (the long b in [0,p) for which
   //   ab = 1 mod p).
   // requires: a and p are coprime
   static long inv (long a, long p) {
      return util.mod(eea(a, p)[0], p);
   }
   
   
   // crt(a,p) produces (using the Chinese Remainder Theorem) the long x in
   //   [0,prod(p)) for which x = a[i] mod p[i] holds for every index i of a and
   //   p.
   // requires: a and p have the same nonzero length
   //           each element of p is a distinct prime
   static long crt (int[] a, int[] p) {
      long m = product(p);
      long x = 0;
      for (int i = 0; i < a.length; i ++) {
         long m_i = m / p[i];
         x += a[i] * m_i * inv(m_i, (long) p[i]);
      }
      return x % m;
   }
   
   
   // nonresidues(p) produces an array containing all (p - 1) / 2 distinct
   //   integers in [0,p) which are quadratic nonresidues modulo p, sorted by
   //   ascending value.
   // requires: p is an odd prime
   static int[] nonresidues (int p) {
      
      // 1. For each i in [0,(p-1)/2), i^2 is distinct modulo p and is congruent
      //    to one of the p-1/2 quadratic residues modulo p. 
      boolean[] isResidue = new boolean[p];
      int max_i = p / 2;
      for (int i = 0; i <= max_i; i ++) {
         isResidue[(int) mod(i * i, p)] = true;
      }
      
      // 2. Loop through isResidue and pick the indices of the nonresidues.
      int[] nonresidue = new int[max_i];
      int index = 0;
      for (int i = 0; i < p; i ++) {
         if (!isResidue[i]) {
            nonresidue[index] = i;
            index ++;
         }
      }
      
      // 3. Return the nonresidues.
      return nonresidue;
   }
   
}