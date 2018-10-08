/*
 *  DHash project implement a storage management
 *  Copyright (C) 2010 - 2018  Daniel Pelaez
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

package co.edu.uniquindio.dhash.resource;

import co.edu.uniquindio.dhash.resource.checksum.ChecksumInputStreamCalculator;
import co.edu.uniquindio.storage.resource.ProgressStatus;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

@Getter
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class FileResource extends BasicResource {

    @Builder(builderMethodName = "withInputStream", builderClassName = "WithInputStreamBuilder")
    FileResource(String id, InputStream inputStream, Long size, String checkSum) {
        super(id, inputStream, size, checkSum);
    }

    public static WithPathBuilder withPath() {
        return new WithPathBuilder();
    }

    public static class WithPathBuilder {
        private String id;
        private String path;

        WithPathBuilder() {
        }

        public WithPathBuilder id(String id) {
            this.id = id;
            return this;
        }

        public WithPathBuilder path(String path) {
            this.path = path;
            return this;
        }

        public FileResource build(ProgressStatus progressStatus) throws IOException {
            Objects.requireNonNull(path);
            Objects.requireNonNull(progressStatus);

            Path filePath = Paths.get(path);

            InputStream inputStream = newInputStream(filePath);
            Long size = getFileSize(filePath);
            String checkSum = getChecksumInputStreamCalculator().calculate(newInputStream(filePath), size, progressStatus);

            return new FileResource(id, inputStream, size, checkSum);
        }

        long getFileSize(Path filePath) throws IOException {
            return Files.size(filePath);
        }

        ChecksumInputStreamCalculator getChecksumInputStreamCalculator() {
            return new ChecksumInputStreamCalculator();
        }

        InputStream newInputStream(Path filePath) throws IOException {
            return Files.newInputStream(filePath);
        }

        public String toString() {
            return "FileResource.WithPathBuilder(id=" + this.id + ", path=" + this.path + ")";
        }
    }
}
