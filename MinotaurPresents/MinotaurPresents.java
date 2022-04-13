import java.util.*;
import java.util.concurrent.locks.ReentrantLock;

class servantThreads implements Runnable{
    private int num;
    int guest_num;
    MinotaurPresents presentBag;

    public servantThreads(int i, int guest_num, MinotaurPresents bag){
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
                    //System.out.println("Servant number " + num + "  added guest "+ g+"'s gift to the list.");
                }
            } 
            int gToC = (int)(Math.random()* (double)(guest_num));
            if(presentBag.contains(gToC)){
                thanks(gToC, guest_num);
            }
            continue;
        }
        
    }

    boolean thanks(int i, int gifts){
        if(presentBag.remove(i)){
            //System.out.println("Present from guest "+i+" removed by servant " + servant);
            if(presentBag.cards.contains(i)){
                return false;
            }else{
                presentBag.cards.add(i);
                return true;
            }
        } else {
            return false;
        }
    }

    boolean inThanks(int i, ArrayList<Integer> cards){
        if(cards.contains(i)){
            return true;
        }else {
            return false;
        }
    }
}

interface Set{
    boolean add(int p);
    boolean remove(int p); 
    boolean contains(int p);
}


public class MinotaurPresents implements Set{
    Node head;

    static class Node {
        boolean present;
        int tag;
        Node next;
        boolean marked;
        ReentrantLock nodeLock;

        public Node (int tag, Node nx)
        {
            this.present = true;
            this.tag = tag;
            this.next = nx;
            this.marked = false;
            this.nodeLock = new ReentrantLock();
        } 
    }

    private boolean validate(Node pred, Node curr) {
        return !pred.marked && !curr.marked && pred.next == curr;
    }

    public boolean add(int tag){
        Node temp = head;
        if(head == null){

            Node node = new Node(tag, null);
            if(head == null){ 
                head = node; 
                return true;
            } else { 
                return false; 
            }
        } else if (head.tag < tag && head.next == null){
            Node node = new Node(tag, null);
            head.next = node;
            return true;
        } else if (head.tag > tag || (head.tag > tag && head.next == null)){
            Node node = new Node(tag, head);
            head = node;
            return true;
        }
        while(true){

            Node pred = head;
            Node curr = head.next;

            while(curr.tag < tag && curr.next != null){
                pred = curr;
                curr = curr.next;
            }
            pred.nodeLock.lock();
            try{
                curr.nodeLock.lock();
                try{
                    if(validate(pred, curr)){
                        if(curr.tag == tag){
                            return false;
                        } else {
                            Node node = new Node(tag, curr);
                            pred.next = node;
                            return true;
                        }
                    }
                }finally{
                    curr.nodeLock.unlock();
                }
            }finally{
                pred.nodeLock.unlock();
            }
        }
    }

    public boolean remove(int tag){
        if(head == null){
            return false;
        }

        while(true){
            Node pred;
            Node curr;
            if(head != null && head.tag == tag && head.next == null){
                pred = head;
                pred.nodeLock.lock();
                try{
                    pred.marked = true;
                    if(pred.next != null){
                        head = pred.next;
                    } else {
                        head = null;
                    }
                } finally {
                    pred.nodeLock.unlock();
                }
                return true;
            } else {
                pred = head;
                curr = head.next;
            }

            while(curr != null && curr.tag < tag && curr.next != null){
                pred = curr;
                curr = curr.next;
            }

            pred.nodeLock.lock();
            try{

                curr.nodeLock.lock();
                try{
                    
                    if(validate(pred, curr)){
                        if(curr.tag != tag){
                            return false;
                        } else {
                            curr.marked = true;
                            pred.next = curr.next;

                            return true;
                        }
                    }
                }finally{
                    curr.nodeLock.unlock();
                }
            }finally{
                pred.nodeLock.unlock();
            }
        }
    }

    public boolean contains(int tag){
        Node curr = head;

        if(head == null){
            return false;
        }

        while (curr.tag < tag){
            if(curr.next == null){
                break;
            }
            curr = curr.next;
            
        }

        return (curr != null && curr.tag == tag && !curr.marked);
    }

    public int numItems(MinotaurPresents presentBag, int gifts){
        if(presentBag.head == null || presentBag.cards.size() == gifts){
            return 0;
        }else{
            return 1;
        }
    }

    public ArrayList<Integer> cards;

    public static void main (int nguests){
        MinotaurPresents presentBag = new MinotaurPresents();
        ArrayList<Thread> sThreads = new ArrayList<Thread>();
        presentBag.cards = new ArrayList<>();
        Thread th;

        for(int i = 0; i < 4; i++){
            servantThreads sTh = new servantThreads(i, nguests, presentBag);
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