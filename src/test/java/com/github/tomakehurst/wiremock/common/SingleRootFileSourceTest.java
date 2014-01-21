/*
 * Copyright (C) 2011 Thomas Akehurst
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
package com.github.tomakehurst.wiremock.common;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileFilter;
import java.util.List;

import static com.github.tomakehurst.wiremock.testsupport.WireMatchers.hasExactly;
import static com.github.tomakehurst.wiremock.testsupport.WireMatchers.hasExactlyIgnoringOrder;
import static org.junit.Assert.assertThat;

public class SingleRootFileSourceTest {

    private Mockery context;

    private File unorderedSourceFolder;

    @Before
    public void setup() {
        setupUnorderedSourceFolder();
    }

    private void setupUnorderedSourceFolder() {
        Mockery context = new Mockery();
        context.setImposteriser(ClassImposteriser.INSTANCE);

        final File unorderedSubdir = getUnorderedSubdir(context);

        final File[] unOrderedFilesAndFolders = {new File("src/test/resources/filesource/three"),
                new File("src/test/resources/filesource/one"),
                new File("src/test/resources/filesource/anothersubdir"),
                new File("src/test/resources/filesource/two"),
                unorderedSubdir};
        final File[] unOrderedFiles = {new File("src/test/resources/filesource/three"),
                new File("src/test/resources/filesource/one"),
                new File("src/test/resources/filesource/two")};
        unorderedSourceFolder = context.mock(File.class, "unOrderedFiles");
        context.checking(new Expectations() {{
            allowing(unorderedSourceFolder).exists(); will(returnValue(true));
            allowing(unorderedSourceFolder).isDirectory(); will(returnValue(true));
            allowing(unorderedSourceFolder).listFiles(); will(onConsecutiveCalls(returnValue(unOrderedFilesAndFolders), returnValue(new File[0])));
            allowing(unorderedSourceFolder).listFiles(with(any(FileFilter.class))); will(onConsecutiveCalls(returnValue(unOrderedFiles), returnValue(new File[0])));
        }});
    }

    private File getUnorderedSubdir(Mockery context) {
        final File[] subdirUnorderedFiles = {new File("src/test/resources/filesource/subdir/four"),
                new File("src/test/resources/filesource/subdir/five"),
                new File("src/test/resources/filesource/subdir/subsubdir")};
        final File unorderedSubdir = context.mock(File.class, "unorderedSubdir");
        context.checking(new Expectations() {{
            allowing(unorderedSubdir).exists(); will(returnValue(true));
            allowing(unorderedSubdir).isDirectory(); will(returnValue(true));
            allowing(unorderedSubdir).getPath(); will(returnValue("src/test/resources/filesource/subdir"));
            allowing(unorderedSubdir).listFiles(); will(onConsecutiveCalls(returnValue(subdirUnorderedFiles), returnValue(new File[0])));
        }});
        return unorderedSubdir;
    }

    @SuppressWarnings("unchecked")
	@Test
	public void listsTextFilesAtTopLevelIgnoringDirectories() {
        SingleRootFileSource fileSource = new SingleRootFileSource("src/test/resources/filesource");
		List<TextFile> files = fileSource.listFiles();
		
		assertThat(files, hasExactlyIgnoringOrder(
                fileNamed("one"), fileNamed("two"), fileNamed("three")));
	}

    @SuppressWarnings("unchecked")
    @Test
    public void orderedListsTextFilesAtTopLevelIgnoringDirectories() {
        SingleRootFileSource fileSource = new SingleRootFileSource(unorderedSourceFolder);
        List<TextFile> files = fileSource.orderedListFiles();

        assertThat(files, hasExactly(
                fileNamed("one"), fileNamed("three"), fileNamed("two")));
    }
	
	@SuppressWarnings("unchecked")
	@Test
	public void listsTextFilesRecursively() {
        SingleRootFileSource fileSource = new SingleRootFileSource("src/test/resources/filesource");
		List<TextFile> files = fileSource.listFilesRecursively();
		
		assertThat(files, hasExactlyIgnoringOrder(
                fileNamed("one"), fileNamed("two"), fileNamed("three"),
                fileNamed("four"), fileNamed("five"), fileNamed("six"),
                fileNamed("seven"), fileNamed("eight")));
	}

    @SuppressWarnings("unchecked")
    @Test
    public void orderedListsTextFilesRecursively() {
        SingleRootFileSource fileSource = new SingleRootFileSource(unorderedSourceFolder);

        List<TextFile> files = fileSource.orderedListFilesRecursively();

        assertThat(files, hasExactly(
                fileNamed("six"), fileNamed("one"), fileNamed("five"),
                fileNamed("four"), fileNamed("eight"), fileNamed("seven"),
                fileNamed("three"), fileNamed("two")));
    }
	
	@Test(expected=RuntimeException.class)
	public void listFilesThrowsExceptionWhenRootIsNotDir() {
		SingleRootFileSource fileSource = new SingleRootFileSource("src/test/resources/filesource/one");
		fileSource.listFiles();
	}
	
	@Test(expected=RuntimeException.class)
	public void listFilesRecursivelyThrowsExceptionWhenRootIsNotDir() {
		SingleRootFileSource fileSource = new SingleRootFileSource("src/test/resources/filesource/one");
		fileSource.listFilesRecursively();
	}
	
	@Test(expected=RuntimeException.class)
	public void writeThrowsExceptionWhenRootIsNotDir() {
		SingleRootFileSource fileSource = new SingleRootFileSource("src/test/resources/filesource/one");
		fileSource.writeTextFile("thing", "stuff");
	}

	private Matcher<TextFile> fileNamed(final String name) {
		return new TypeSafeMatcher<TextFile>() {

			@Override
			public void describeTo(Description desc) {
			}

			@Override
			public boolean matchesSafely(TextFile textFile) {
				return textFile.name().equals(name);
			}

            @Override
            public String toString() {
                return name;
            }
        };
	}
}
