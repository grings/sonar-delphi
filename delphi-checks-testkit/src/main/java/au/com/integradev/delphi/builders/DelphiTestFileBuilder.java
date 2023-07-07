/*
 * Sonar Delphi Plugin
 * Copyright (C) 2019-2022 Integrated Application Development
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02
 */
package au.com.integradev.delphi.builders;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import au.com.integradev.delphi.DelphiProperties;
import au.com.integradev.delphi.core.DelphiLanguage;
import au.com.integradev.delphi.file.DelphiFile;
import au.com.integradev.delphi.file.DelphiFile.DelphiInputFile;
import au.com.integradev.delphi.file.DelphiFileConfig;
import au.com.integradev.delphi.preprocessor.search.SearchPath;
import au.com.integradev.delphi.type.factory.TypeFactory;
import au.com.integradev.delphi.utils.DelphiUtils;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import org.apache.commons.io.FileUtils;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.batch.fs.internal.TestInputFileBuilder;
import org.sonar.api.utils.log.Logger;
import org.sonar.api.utils.log.Loggers;
import org.sonar.plugins.communitydelphi.api.ast.DelphiAst;

public abstract class DelphiTestFileBuilder<T extends DelphiTestFileBuilder<T>> {
  private static final Logger LOG = Loggers.get(DelphiTestFileBuilder.class);
  private final StringBuilder declaration = new StringBuilder();
  private final StringBuilder implementation = new StringBuilder();

  private File baseDir = FileUtils.getTempDirectory();
  private int declCount;

  public T appendDecl(String value) {
    declaration.append(value).append("\n");
    declCount++;
    return getThis();
  }

  public T appendImpl(String value) {
    implementation.append(value).append("\n");
    return getThis();
  }

  protected String getDeclaration() {
    return declaration.toString();
  }

  protected String getImplementation() {
    return implementation.toString();
  }

  public void setBaseDir(File baseDir) {
    this.baseDir = baseDir;
  }

  public DelphiAst parse() {
    DelphiFile file = DelphiInputFile.from(inputFile(), mockConfig());
    return file.getAst();
  }

  public InputFile inputFile() {
    StringBuilder source = getSourceCode();

    InputFile inputFile;
    try {
      File file = File.createTempFile(getFilenamePrefix(), "." + getFileExtension(), baseDir);
      file.deleteOnExit();

      try (FileWriter fileWriter = new FileWriter(file, UTF_8)) {
        fileWriter.write(source.toString());
        fileWriter.flush();
      }

      inputFile =
          TestInputFileBuilder.create("moduleKey", baseDir, file)
              .setContents(DelphiUtils.readFileContent(file, UTF_8.name()))
              .setLanguage(DelphiLanguage.KEY)
              .setType(InputFile.Type.MAIN)
              .build();
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }

    return inputFile;
  }

  public DelphiInputFile delphiFile() {
    return DelphiInputFile.from(inputFile(), mockConfig());
  }

  public DelphiInputFile delphiFile(DelphiFileConfig fileConfig) {
    return DelphiInputFile.from(inputFile(), fileConfig);
  }

  private StringBuilder getSourceCode() {
    return generateSourceCode();
  }

  public void printSourceCode() {
    StringBuilder source = getSourceCode();
    Reader reader = new StringReader(source.toString());
    BufferedReader lineReader = new BufferedReader(reader);
    String line;
    int lineNumber = 0;
    try {
      while ((line = lineReader.readLine()) != null) {
        LOG.info(String.format("%03d %s", ++lineNumber, line));
      }
    } catch (IOException e) {
      throw new RuntimeException("Failed to print source code.", e);
    }
  }

  protected int getDeclCount() {
    return declCount;
  }

  public abstract int getOffsetDecl();

  public abstract int getOffset();

  protected abstract T getThis();

  protected abstract StringBuilder generateSourceCode();

  protected abstract String getFilenamePrefix();

  protected abstract String getFileExtension();

  public static DelphiTestFileBuilder.ResourceBuilder fromResource(String path) {
    return new ResourceBuilder(DelphiUtils.getResource(path));
  }

  public static class ResourceBuilder extends DelphiTestFileBuilder<ResourceBuilder> {
    private final File resource;

    private ResourceBuilder(File resource) {
      this.resource = resource;
    }

    @Override
    public int getOffsetDecl() {
      return 0;
    }

    @Override
    public int getOffset() {
      return 0;
    }

    @Override
    protected ResourceBuilder getThis() {
      return this;
    }

    @Override
    public InputFile inputFile() {
      InputFile inputFile;
      try {
        File baseDir = resource.getParentFile();
        inputFile =
            TestInputFileBuilder.create("moduleKey", baseDir, resource)
                .setContents(DelphiUtils.readFileContent(resource, UTF_8.name()))
                .setLanguage(DelphiLanguage.KEY)
                .setType(InputFile.Type.MAIN)
                .build();
      } catch (IOException e) {
        throw new UncheckedIOException(e);
      }

      return inputFile;
    }

    @Override
    protected StringBuilder generateSourceCode() {
      try {
        return new StringBuilder(DelphiUtils.readFileContent(resource, UTF_8.name()));
      } catch (IOException e) {
        throw new UncheckedIOException(e);
      }
    }

    @Override
    public ResourceBuilder appendDecl(String value) {
      throw new UnsupportedOperationException("Not supported for ResourceBuilder");
    }

    @Override
    public ResourceBuilder appendImpl(String value) {
      throw new UnsupportedOperationException("Not supported for ResourceBuilder");
    }

    @Override
    protected String getFilenamePrefix() {
      throw new UnsupportedOperationException("Not supported for ResourceBuilder");
    }

    @Override
    protected String getFileExtension() {
      throw new UnsupportedOperationException("Not supported for ResourceBuilder");
    }
  }

  private static DelphiFileConfig mockConfig() {
    TypeFactory typeFactory =
        new TypeFactory(
            DelphiProperties.COMPILER_TOOLCHAIN_DEFAULT, DelphiProperties.COMPILER_VERSION_DEFAULT);
    DelphiFileConfig mock = mock(DelphiFileConfig.class);
    when(mock.getEncoding()).thenReturn(StandardCharsets.UTF_8.name());
    when(mock.getTypeFactory()).thenReturn(typeFactory);
    when(mock.getSearchPath()).thenReturn(SearchPath.create(Collections.emptyList()));
    when(mock.getDefinitions()).thenReturn(Collections.emptySet());
    return mock;
  }
}