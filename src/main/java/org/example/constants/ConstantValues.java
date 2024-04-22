package org.example.constants;

public class ConstantValues {
    private ConstantValues(){
        throw new ExceptionInInitializerError();
    }
    public static final int IRON_MAX_CAPACITY = 100;
    public static final int COOPER_MAX_CAPACITY = 100;
    public static final int BOLT_MAX_CAPACITY = 100;


    public static final String MATERIAL_CREATED = "Material is created";
    public static final String MATERIAL_IS_PRESENT = "Material is present in the Warehouse.";
    public static final String WAREHOUSE_DOES_NOT_EXIST = "Warehouse does not exist.";


    public static final String WAREHOUSE_MATERIAL_REMOVAL = "WAREHOUSE: MATERIAL REMOVAL";
    public static final String WAREHOUSE_MATERIAL_ADD =  "WAREHOUSE: MATERIAL ADDITION";

    public static final String MATERIAL_IS_FULL =  "MATERIAL IS FULL";
    public static final String MATERIAL_IS_ZERO =  "MATERIAL IS ZERO";
    public static final String MATERIAL_IS_SUBTRACTED =  "MATERIAL IS SUBTRACTED";
    public static final  String MATERIAL_IS_ADDED = "MATERIAL IS ADDED";

    public static final String WAREHOUSE_IS_CREATED =  "WAREHOUSE IS CREATED";

}
