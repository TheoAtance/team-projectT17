package entity;

import java.util.List;


public class QuerySpec {
    public List<String> must;
    public List<String> should;
    public List<String> avoid;

    public Integer budgetMax;
    public Double radiusKm;
    public Boolean openNow;

    public List<String> cuisine;
    public List<String> dietary;
    public String locationHint;
}
