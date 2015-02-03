package fr.insensa.decouverte_patrimoine.android;

import java.util.ArrayList;

public class ParentListParcours
{
    private String name;
    private String checkedtype;
    private boolean checked;

    // ArrayList to store child objects
    private ArrayList<ChildListParcours> childListParcours;

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getCheckedType()
    {
        return checkedtype;
    }

    public void setCheckedType(String checkedtype)
    {
        this.checkedtype = checkedtype;
    }


    public boolean isChecked()
    {
        return checked;
    }
    public void setChecked(boolean checked)
    {
        this.checked = checked;
    }

    // ArrayList to store child objects
    public ArrayList<ChildListParcours> getChildListParcours()
    {
        return childListParcours;
    }

    public void setChildListParcours(ArrayList<ChildListParcours> childListParcours)
    {
        this.childListParcours = childListParcours;
    }
}