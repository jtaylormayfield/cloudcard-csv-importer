package com.sharptop.cloudcard.csvimporter;

import java.util.Arrays;

/**
 * Created by Brandon on 7/6/2017.
 */
public class CardHolder {

    private static Integer organizationId;

    private static String[] header;

    private static final int EMAIL_INDEX = 0;
    private static final int ID_INDEX = 1;
    private String email;
    private String id;
    private String inputString;
    private String delimiter;
    String[] fieldValues;

    CardHolder() {

    }

    CardHolder(String delimiter, String inputString) {

        this.inputString = inputString;
        this.delimiter = delimiter;
        this.parseInputString();
    }

    public void setDelimiter(String delimiter) {

        this.delimiter = delimiter;
    }

    String getEmail() {

        return email;
    }

    void setEmail(String inputEmail) {

        email = inputEmail;
    }

    public String getId() {

        return id;
    }

    void setId(String inputID) {

        id = inputID;
    }

    public static int getOrganizationId() {

        return organizationId;
    }

    public static void setOrganizationId(int organizationId) throws IllegalAccessException {

        if (CardHolder.organizationId != null) {
            throw new IllegalAccessException("Organization id can only be set once and never modified.");
        }

        CardHolder.organizationId = organizationId;
    }

    public static String[] getHeader() {

        return header;
    }

    // TODO: This is also very stupid
    public static void setHeader(String[] header) {
    	Arrays.parallelSetAll(header, (i) -> header[ i ].trim());
        CardHolder.header = header;
    }

    public static String csvHeader() {

        return String.join(", ", header);
    }

    public void parseInputString() {

        fieldValues = inputString.split(delimiter);
        Arrays.parallelSetAll(fieldValues, (i) -> fieldValues[ i ].trim());

        email = fieldValues[ EMAIL_INDEX ];
        id = fieldValues[ ID_INDEX ];
    }

    public String toJSON() {

        return "{ \"email\":\"" + email + "\"," + "\"organization\":{\"id\":" + organizationId + "}," + "\"customFields\":" + getCustomFieldsAsJSON() + ", " + "\"identifier\":\"" + id + "\" }";
    }

    private String getCustomFieldsAsJSON() {

        StringBuilder customFieldsAsJSON = new StringBuilder("{");
        for (int i = 2; i < header.length; i++) {
            customFieldsAsJSON.append("\"" + header[ i ] + "\":\"" + fieldValues[ i ].replaceAll("\"", "") + "\"");
            if (i < header.length - 1) {
                customFieldsAsJSON.append(",");
            }
        }
        customFieldsAsJSON.append("}");
        return customFieldsAsJSON.toString();
    }

    @Override
    public String toString() {

        return String.join(", ", fieldValues);
    }

    public boolean validate() {

        return (email.isEmpty() || id.isEmpty()) ? false : true;
    }
}
