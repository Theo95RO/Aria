package com.gmail.btheo95.aria.model;

/**
 * Created by btheo on 02.11.2016.
 */

public class IPv4 {

    private short cell1;
    private short cell2;
    private short cell3;
    private short cell4;

    public IPv4(){
        this.cell1 = 0;
        this.cell2 = 0;
        this.cell3 = 0;
        this.cell4 = 0;
    }

    public IPv4(short cell1, short cell2, short cell3, short cell4){
        this.cell1 = cell1;
        this.cell2 = cell2;
        this.cell3 = cell3;
        this.cell4 = cell4;
    }

    public IPv4 (IPv4 ip) {
        this.cell1 = ip.cell1;
        this.cell2 = ip.cell2;
        this.cell3 = ip.cell3;
        this.cell4 = ip.cell4;
    }
    public IPv4(int cell1, int cell2, int cell3, int cell4){
        this.cell1 = (short)cell1;
        this.cell2 = (short)cell2;
        this.cell3 = (short)cell3;
        this.cell4 = (short)cell4;
    }

    public void increment(){
        cell4++;
        if (cell4 >= 256){
            cell4 = 0;
            cell3++;

            if (cell3 >= 256){
                cell3 = 0;
                cell2++;

                if (cell2 >= 256){
                    cell2 = 0;
                    cell1++;

                    if(cell1 >= 256) {
                        cell1 = 0;
                    }
                }
            }
        }
    }

    public short getCell1() {
        return cell1;
    }

    public void setCell1(short cell1) {
        this.cell1 = cell1;
    }

    public short getCell2() {
        return cell2;
    }

    public void setCell2(short cell2) {
        this.cell2 = cell2;
    }

    public short getCell3() {
        return cell3;
    }

    public void setCell3(short cell3) {
        this.cell3 = cell3;
    }

    public short getCell4() {
        return cell4;
    }

    public void setCell4(short cell4) {
        this.cell4 = cell4;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (!IPv4.class.isAssignableFrom(obj.getClass())) {
            return false;
        }
        final IPv4 other = (IPv4) obj;
        if (other.getCell1() == cell1
                && other.getCell2() == cell2
                && other.getCell3() == cell3
                && other.getCell4() == cell4) {
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return cell1 + "." + cell2 + "." + cell3 + "." + cell4;
    }
}
