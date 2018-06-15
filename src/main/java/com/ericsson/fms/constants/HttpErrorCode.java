package com.ericsson.fms.constants;

public enum HttpErrorCode {
    CLIENT_ERROR("GisService-400001"),
    START_OR_END_TIMENOTFOUND_ERROR("GisService-400002"),
    ENDTIME_BEFORE_STARTTIME_ERROR("GisService-400003"),
    TIME_FORMAT_ERROR("GisService-400004"),
    TIME_SEATCH_LIMITE_ERROR("GisService-400005"),
    PAGESIZE_FORMAT_ERROR("GisService-400006"),
    INDEX_NOT_FOUND_ERROR("GisService-400007"),
    SEARCH_LIMITE_ERROR("GisService-400008"),

    MISS_PARAMETER_VINVRN("GisService-400009"),
    MISS_PARAMETER_GEOVINVRN("GisService-400010"),
    TYPE_NOT_FOUND_ERROR("GisService-4000011"),
    TYPE_FORMAT_ERROR("GisService-4000012"),
    GEOFENCE_INVALID_ERROR("GisService-4000013"),

    SERVER_OK("GisService-000000"),
    SERVER_ERROR("GisService-500001"),
	PERMISSION_DENIED("GisService-500002"),

	MISS_PARAMETER("GisService-600000");


    private String codeString;

    private HttpErrorCode(String codeString) {
        this.codeString = codeString;
    }

    public String getCodeString() {
        return codeString;
    }
}
