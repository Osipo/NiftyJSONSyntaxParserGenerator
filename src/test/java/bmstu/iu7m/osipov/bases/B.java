package bmstu.iu7m.osipov.bases;

public class B extends A{

    private A a;

    public B(){}

    public B(A a){
        this.a = a;
    }
    @Override
    public void M1() {
        this.s = 11;
        super.M1();
        System.out.println("after M1 s = "+this.s);
        System.out.println("M1 called from child");
    }

    public void changeCOL(){
        this.items.clear();
    }

    @Override
    protected void M2() {
        System.out.println("B.M2 call (child)");
    }
}
