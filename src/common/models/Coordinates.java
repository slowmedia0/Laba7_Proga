package common.models;

import java.io.Serializable;


public class Coordinates implements Serializable {
    private Long x;
    private Double y;

    
    public Coordinates(Long x,Double y) {
        this.x = x;
        this.y = y;
    }

    
    public Long getX() {
        return x;
    }

    
    public void setX(Long x) {
        this.x = x;
    }

    
    public Double getY() {
        return y;
    }

    
    public void setY(Double y) {
        this.y = y;
    }

    
    @Override
    public String toString(){
        return "Coordinates { x = " + x + "; y = " + y +" }";
    }
}