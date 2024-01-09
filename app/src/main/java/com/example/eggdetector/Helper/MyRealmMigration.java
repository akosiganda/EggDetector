package com.example.eggdetector.Helper;
import java.util.Date;
import io.realm.DynamicRealm;
import io.realm.FieldAttribute;
import io.realm.RealmMigration;
import io.realm.RealmObjectSchema;
import io.realm.RealmSchema;
import io.realm.exceptions.RealmMigrationNeededException;

public class MyRealmMigration implements RealmMigration {

    private static final long PREVIOUS_SCHEMA_VERSION = 1; // replace with your actual previous version
    private static final long NEW_SCHEMA_VERSION = 2; // replace with your new schema version

    @Override
    public void migrate(DynamicRealm realm, long oldVersion, long newVersion) {
        RealmSchema schema = realm.getSchema();

        // Migrate from previous version to new version
        if (oldVersion == PREVIOUS_SCHEMA_VERSION) {
            // Add the RealmTransaction class
            RealmObjectSchema transactionSchema = schema.create("RealmTransaction")
                    .addField("type", String.class)
                    .addField("date", Date.class)
                    .addField("count", int.class)
                    .addField("id", long.class, FieldAttribute.PRIMARY_KEY)
                    .addField("newField", String.class); // Add any new fields as needed

            // Increment the version after applying migration
            oldVersion++;
        }

        // Handle other version increments if necessary
        // ...

        // Ensure to set the new version in the Realm configuration
        if (realm.getVersion() != newVersion) {
            throw new RealmMigrationNeededException(realm.getPath(), "Migration missing!");
        }
    }

    @Override
    public boolean equals(Object obj) {
        return this == obj || (obj != null && getClass() == obj.getClass());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
