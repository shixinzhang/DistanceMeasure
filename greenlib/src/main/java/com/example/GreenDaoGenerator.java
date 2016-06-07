package com.example;

import java.io.IOException;

import de.greenrobot.daogenerator.DaoGenerator;
import de.greenrobot.daogenerator.Entity;
import de.greenrobot.daogenerator.Schema;

public class GreenDaoGenerator {

    private GreenDaoGenerator() {}

    public static void main(String[] args) throws Exception {
        int version = 1;
        String defaultPackage = "net.sxkeji.xddistance";
        Schema schema = new Schema(version,defaultPackage);
        addPictureInfoTable(schema);
        new DaoGenerator().generateAll(schema,"./app/src/main/java-gen");
    }

    private static void addPictureInfoTable(Schema schema) {
        Entity entity = schema.addEntity("PictureInfo");
        entity.addIdProperty();
        entity.addStringProperty("path").notNull();
        entity.addStringProperty("distance").notNull();
        entity.addStringProperty("time").notNull();
        entity.addStringProperty("tips");
    }
}
