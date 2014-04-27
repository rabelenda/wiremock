/*
 * Copyright (C) 2011 Thomas Akehurst
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package com.github.tomakehurst.wiremock.common;

import com.google.common.base.Function;
import com.google.common.io.Files;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Charsets.UTF_8;
import static com.google.common.collect.Iterables.transform;
import static com.google.common.collect.Lists.newArrayList;

public class TextFile {

    private final File file;
    private String relativePath;

    public TextFile(final String rootPath, final String relativePath) {
        this.relativePath = relativePath;
        file = new File(rootPath + File.separator + relativePath);
    }

    public TextFile(final File file) {
        this.file = file;
    }

    public String readContents() {
        try {
            final String json = Files.toString(file, UTF_8);
            return json;
        } catch (final IOException ioe) {
            throw new RuntimeException(ioe);
        }
    }

    public String name() {
        return file.getName();
    }

    public String path() {
        return file.getPath();
    }

    public String relativePath() {
        return relativePath;
    }

    @Override
    public String toString() {
        return file.getPath();
    }

    public static ArrayList<String> getRelativePaths(List<TextFile> fileList) {
        return newArrayList(transform(fileList, new Function<TextFile, String>() {
            public String apply(TextFile input) {
                return input.relativePath();
            }
        }));
    }
}
