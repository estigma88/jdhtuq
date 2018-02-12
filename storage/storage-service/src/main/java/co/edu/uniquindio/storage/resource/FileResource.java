/*
 *  StorageService project defined all services an storage management
 *  Copyright (C) 2010  Daniel Pelaez, Daniel Lopez, Hector Hurtado
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package co.edu.uniquindio.storage.resource;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Map;

import co.edu.uniquindio.utils.EscapeChars;
import co.edu.uniquindio.utils.hashing.DigestGenerator;

/**
 * The <code>FileResource</code> class implement all services to management
 * files in disk
 * 
 * @author Daniel Pelaez
 * @version 1.0, 17/06/2010
 * @since 1.0
 * 
 */
public class FileResource extends SerializableResource {
	/**
	 * Serial
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * File in disk
	 */
	private transient File file;
	/**
	 * Path destination from persist
	 */
	private String directory = "dhash/";
	/**
	 * Bytes file
	 */
	private byte[] bytesFile;

	/**
	 * Builds a FileResource from File in disk
	 * 
	 * @param file
	 *            File in disk
	 */
	public FileResource(File file) {
		super(file.getName());

		this.file = file;
	}

	/**
	 * Delete file in disk
	 */
	public void delete(Map<String, Object> params) throws ResourceException {
		if (file != null) {
			file.delete();
		}
	}

	/**
	 * Gets check sum based in DigestGenerator from bytes file
	 */
	public String getCheckSum() {
		FileInputStream fileInputStream;
		byte[] bytes;

		try {

			if (file != null && file.exists()) {
				bytes = new byte[(int) file.length()];

				fileInputStream = new FileInputStream(file);

				fileInputStream.read(bytes);
			} else {
				bytes = bytesFile;
			}

			return DigestGenerator.getInstance().getCheckSum(bytes);
		} catch (FileNotFoundException e) {
			throw new IllegalStateException("File not found", e);
		} catch (IOException e) {
			throw new IllegalStateException("Error reading file", e);
		}
	}

	/**
	 * Gets object serializable from file bytes
	 */
	public byte[] getSerializable() {
		ByteArrayOutputStream byteArrayOutputStream;
		ObjectOutputStream objectOutputStream;
		FileInputStream fileInputStream;
		try {

			if (file != null && file.exists()) {
				bytesFile = new byte[(int) file.length()];

				fileInputStream = new FileInputStream(file);

				fileInputStream.read(bytesFile);
			}

			byteArrayOutputStream = new ByteArrayOutputStream();

			objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);

			objectOutputStream.writeObject(this);

			return byteArrayOutputStream.toByteArray();

		} catch (IOException e) {
			throw new IllegalStateException("Error reading file", e);
		}
	}

	/**
	 * Persist file in disk. The path for persist is defined so:
	 * directory/MANAGER_NAME/ where directory for default is dhash/ and
	 * MANAGER_NAME is obtained like param in Map params
	 */
	public void persist(Map<String, Object> params) throws ResourceException {

		if (params == null) {
			throw new IllegalArgumentException("The params must not null");
		}
		if (params.get(ResourceParams.MANAGER_NAME.name()) == null) {
			throw new IllegalArgumentException(
					"The params must to contain param MANAGER_NAME");
		}
		if (file != null && file.exists()) {
			return;
		}

		if (bytesFile == null) {
			return;
		}

		FileOutputStream fileOutputStream;
		StringBuilder directoryPath;
		File directoryFile;
		try {
			directoryPath = new StringBuilder(directory);
			directoryPath.append(EscapeChars.forHTML(params.get(
					ResourceParams.MANAGER_NAME.name()).toString(), true));
			directoryPath.append("/");

			directoryFile = new File(directoryPath.toString());
			directoryFile.mkdirs();

			file = new File(directoryFile, EscapeChars.forHTML(key, false));

			fileOutputStream = new FileOutputStream(file);

			fileOutputStream.write(bytesFile);

			fileOutputStream.close();

			bytesFile = null;

		} catch (IOException e) {
			throw new IllegalStateException("Error reading file", e);
		}
	}

	/**
	 * Gets directory where persist
	 * 
	 * @return Path
	 */
	public String getDirectory() {
		return directory;
	}

	/**
	 * Sets directory where persist
	 * 
	 * @param directory
	 *            Path
	 */
	public void setDirectory(String directory) {
		this.directory = directory;
	}

}
