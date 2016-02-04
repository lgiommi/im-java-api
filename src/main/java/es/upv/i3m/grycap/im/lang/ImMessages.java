/**
 * Copyright (C) GRyCAP - I3M - UPV 
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package es.upv.i3m.grycap.im.lang;

public class ImMessages {

    // Exceptions
    public static final String EXCEPTION_NULL_VALUE = "Null value not returned";
    public static final String EXCEPTION_INFRASTRUCTURE_OUTPUTS = "Error retrieving the infrastructure outputs";
    public static final String EXCEPTION_TOSCA_NOT_SUPPORTED = "TOSCA content type not supported";
    public static final String EXCEPTION_AUTHORIZATION_FILE_NOT_FOUND = "Authorization file not specified";
    public static final String EXCEPTION_PATH_VALUE = "Null or empty path value is not accepted";
    public static final String EXCEPTION_AUTHORIZATION_FILE_ERROR = "Error reading the authorization file";

    // Warning
    public static final String WARNING_NULL_OR_EMPTY_PARAMETER_VALUES = "Null or empty list passed as parameter values";
    public static final String WARNING_NULL_PARAMETER_NAME = "Null string list passed as parameter name";
    public static final String WARNING_NULL_SERVICE_RESPONSE = "Null response passed to the service response";
    public static final String WARNING_NULL_SERVICE_RESULT = "Null result set in the service response";

    // Info
    public static final String INFO_EMPTY_PUT_CONTENT = "Empty PUT body content";
    public static final String INFO_EMPTY_POST_CONTENT = "Empty POST body content";

    private ImMessages() {
    }
}