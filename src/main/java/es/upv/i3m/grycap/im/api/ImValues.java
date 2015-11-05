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
package es.upv.i3m.grycap.im.api;

/**
 * Enumerates static values related with the Infrastructure.<br>
 */
public enum ImValues {

	START("start"),
	STOP("stop"),
	CONTMSG("contmsg"),
	RADL("radl"),
	STATE("state"),
	RECONFIGURE("reconfigure");
			
	private final String value;

	ImValues(String value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return value;
	}
	
	/**
	 * Return True if the string passed matches the internal value of the
	 * instance created
	 * 
	 * @param value
	 *            : string to compare
	 * @return : True if equal, false otherwise
	 */
	public boolean equals(String value){
		return this.value.equals(value);
	}
}
