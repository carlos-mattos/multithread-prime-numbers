package org.example;

import java.io.PrintWriter;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PrimeNumberGenerator implements Runnable{
    private static int MAX = 10000;
    protected int from, to;
    List<Integer> primeList = new LinkedList<>();
    private static final Object mutex = new Object();

    public PrimeNumberGenerator(int from,int to) {
        this.from = from;
        this.to = to;
    }

    public List<Integer> getPrimes() {
        return primeList;
    }

    public void run() {
        synchronized(mutex) {
            boolean isPrime;

            for (int i = from; i <= to; i++) {
                if ((i == 1) || (i == 0)) {
                    continue;
                }

                isPrime = true;

                for (int j = 2; j <= (i / 2); ++j) {
                    if ((i % j) == 0) {
                        isPrime = false;
                        break;
                    }
                }

                if (isPrime) {
                    primeList.add(i);
                }
            }
        }
    }

    public static void main(String[] args) throws Exception {
        int threadCount = (int)Math.floor(Math.random() * ((MAX * 1.5) - 2) + 1);
        List<PrimeNumberGenerator> aux = new ArrayList<PrimeNumberGenerator>();

        if (MAX % threadCount != 0){
            while (MAX % threadCount != 0) {
                threadCount--;
            }
        }

        Thread[] threads = new Thread[threadCount];
        int rangerPerThread = MAX / threadCount;

        for(int i = 0; i < threadCount; i++) {
            int from = i * rangerPerThread + 1;
            int to = i * rangerPerThread + rangerPerThread;

            aux.add(new PrimeNumberGenerator(from, to));

            threads[i] = new Thread(aux.get(i));
            threads[i].start();
        }

        PrintWriter writer = new PrintWriter("primes.txt", "UTF-8");

        for (int i = 0; i < threadCount; i++) {
            try {
                threads[i].join();

                if( aux.get(i).getPrimes().size() != 0){
                    aux.get(i).getPrimes().forEach(primeList -> writer.print(primeList + " "));
                }

            }
            catch (InterruptedException ex) {
                Logger.getLogger(PrimeNumberGenerator.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        writer.close();
    }
}