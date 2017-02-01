package com.example.gauti.cobra.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by mobilefactory on 31/01/2017.
 */

@DatabaseTable(tableName = "alerte")
public class Alerte {

    @DatabaseField(generatedId = true)
    public int id;

    @DatabaseField
    public String title;

    @DatabaseField
    public String date;

    @DatabaseField
    public Double latitude;

    @DatabaseField
    public Double longitude;
}
