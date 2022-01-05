package bmstu.iu7m.osipov.bases;

import java.util.ArrayList;
import java.util.List;

public class A {
    protected int s = 0;
    protected int s2 = 0;

    protected List<String> items;

    public A(){
        this.items = new ArrayList<>();
    }

    public void M1(){
        if(s2 == 13){
            System.out.println("s2 was set = 13");
            System.out.println("may be called from switch > case 0: B.M1()");
            return;
        }
        switch (this.s){
            case 0: {
                M2();
                this.s2 = 13;
                B b2 = new B();

                b2.M1();
                return;
            }
            case 11:{
                System.out.println("called with child value of s = "+this.s);
                this.s = 111;
                break;
            }
            case 13:{
                System.out.println("called ");
                break;
            }
        }
    }

    public void COL(){
        items.add("S1");
        items.add("S2");
        items.forEach(x -> System.out.println(x+ " "));
        B b = new B();
        b.changeCOL();
        items.forEach(x -> System.out.println(x+ " "));
    }


    protected void M2(){
        System.out.println("A.M2() call");
    }
}
