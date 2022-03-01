package bmstu.iu7m.osipov.services.grammars.xmlMeta;

public class SizeRelationItem {
    private String propName;

    private Object parent;

    private Object child;

    private double ratio;

    public SizeRelationItem() {
        this(0.0d);
    }

    public SizeRelationItem(double r){
        this.ratio = r;
        this.propName = null;
        this.child = null;
        this.parent = null;
    }

    public String getPropName() {
        return propName;
    }

    public Object getParent() {
        return parent;
    }

    public Object getChild() {
        return child;
    }

    public void setPropName(String propName) {
        this.propName = propName;
    }

    public void setParent(Object parent) {
        this.parent = parent;
    }

    public void setChild(Object child) {
        this.child = child;
    }

    public void setRatio(double ratio) {
        this.ratio = ratio;
    }

    public double getRatio() {
        return ratio;
    }

    @Override
    public String toString(){
        return "{\n\t" +
                "propName: '"+propName+"',\n\t" +
                "ratio: '" + ratio + "\n}";
    }
}
