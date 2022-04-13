import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicIntegerArray;
import java.util.concurrent.atomic.AtomicMarkableReference;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.ReentrantLock;

class servantThreads1 implements Runnable{
    private int num;
    int guest_num;
    MinoPresents presentBag;

    public servantThreads1(int i, int guest_num, MinoPresents bag){
        this.num = i;
        this.guest_num = guest_num;
        this.presentBag = bag;
    }

    @Override
    public void run(){
        while(presentBag.cards.size() < guest_num){
            int g = (int)(Math.random()* (double)(guest_num));
            if(!presentBag.contains(g) && !presentBag.cards.contains(g)){
                if(presentBag.add(g)){
                   // System.out.println("Servant number " + num + "  added guest "+ g +"'s gift to the list.");
                }
            } 

            int gToC = (int)(Math.random()* (double)(guest_num));
            if(presentBag.contains(gToC)){
                if(presentBag.remove(gToC)){
                    if(!inThanks(gToC, presentBag.cards)){
                        //System.out.println("Present from guest "+gToC+" removed by servant " + num);
                        presentBag.cards.add(gToC);
                    }
                }
            }
        }
    }

    boolean thanks(int i, int gifts){
        if(presentBag.remove(i)){
            if(presentBag.cards.contains(i)){
                return false;
            }
            presentBag.cards.add(i);
            return true;
        }
        return false;
    }

    boolean inThanks(int i, ArrayList<Integer> cards){
        if(cards.contains(i)){
            return true;
        }
        return false;
    }
}

interface Set1{
    boolean add(int p);
    boolean remove(int p); 
    boolean contains(int p);
}


public class MinoPresents implements Set1{
    Node head;

    static class Node{
        int tag;
        public AtomicMarkableReference<Node> next;

        public Node(int tag)
        {
            this.tag = tag;
            this.next = null;
        }
    }

    public void List(){
        head = new Node(Integer.MIN_VALUE);
        head.next = new AtomicMarkableReference<Node>(new Node(Integer.MAX_VALUE), false);
    }

    public boolean add(int tag){
        while(true){

            Window window = find(head, tag);
            Node pred = window.pred, curr = window.curr;

            if(curr.tag == tag){
                return false;
            }else {
                Node node = new Node(tag);
                node.next = new AtomicMarkableReference<Node>(curr, false);
                if(pred.next.compareAndSet(curr, node, false, false)){
                    return true;
                }
            }
        }
    }

    public boolean remove(int tag){
        boolean snip;

        while(true){
            Window window = find(head, tag);
            Node pred = window.pred, curr = window.curr;
            if(curr.tag != tag){
                return false;
            } else {
                Node succ = curr.next.getReference();
                snip = curr.next.compareAndSet(succ, succ, false, true);
                if(!snip){
                    continue;
                }
                pred.next.compareAndSet(curr, succ, false, false);
                return true;
            }
        }
    }

    public boolean contains(int tag){
        boolean[] marked = {false};
        Node curr = head;
        while(curr.tag < tag) {
            curr = curr.next.getReference();
            if(curr.tag == Integer.MAX_VALUE){
                return false;
            }

            Node succ = curr.next.get(marked);
        }
        return (curr.tag == tag && !marked[0]);
    }

    /*public boolean compareAndSet(int tag, Node expected, Node newNode, boolean expectedMark, boolean newMark){
        AtomicReference<Long> pre = new AtomicReference(tag);


    }*/

    class Window{
        public Node pred, curr;

        Window(Node myPred, Node myCurr){
            pred = myPred;
            curr = myCurr;
        }
    }

    public Window find(Node head, int tag){
        Node pred = null, curr = null, succ = null;
        boolean[] marked = {false};
        boolean snip;
        
        retry: while(true){
            pred = head;
            curr = pred.next.getReference();
            while(true){
                //System.out.println(counter);
                if(curr.tag == Integer.MAX_VALUE && curr == pred.next.getReference()){
                    return new Window(pred, curr);
                }

                succ = curr.next.get(marked);
                while(marked[0]){
                    snip = pred.next.compareAndSet(curr, succ, false, false);
                    if(!snip) {
                        continue retry;
                    }
                    curr = succ;
                    succ = curr.next.get(marked);
                }

                if(curr.tag >= tag){
                    return new Window(pred, curr);
                }
                pred = curr;
                curr = succ;
            }
        }
    }

    public int numItems(MinoPresents presentBag, int gifts){
        if(presentBag.head == null || presentBag.cards.size() == gifts){
            return 0;
        }else{
            return 1;
        }
    }

    public ArrayList<Integer> cards;

    public static void main (int nguests){
        MinoPresents presentBag = new MinoPresents();
        presentBag.List();
        ArrayList<Thread> sThreads = new ArrayList<Thread>();
        presentBag.cards = new ArrayList<Integer>();
        Thread th;

        for(int i = 0; i < 4; i++){
            servantThreads1 sTh = new servantThreads1(i, nguests, presentBag);
            th = new Thread(sTh);
            sThreads.add(th);
            th.start();
        }

        for(Thread t : sThreads){
            
            try {
                t.join();
            } catch(InterruptedException e){
                e.printStackTrace();
            }
        }

        System.out.println("Guest Presents: "+ nguests + "\nItems left in bag: "+ presentBag.numItems(presentBag, nguests)+"\nCards written: "+presentBag.cards.size());

    }
}