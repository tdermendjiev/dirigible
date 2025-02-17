/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2022 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.database.sql.dialects.hana;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import org.eclipse.dirigible.database.sql.DataType;
import org.eclipse.dirigible.database.sql.SqlFactory;
import org.eclipse.dirigible.database.sql.Table;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

public class HanaCreateTableBuilderTest {

  /**
   * Creates the table generic.
   */
  @Test
  public void createTableGeneric() {
    String sql = SqlFactory.getNative(new HanaSqlDialect())
        .create()
        .table("CUSTOMERS")
        .column("ID", DataType.INTEGER, true, false, false)
        .column("FIRST_NAME", DataType.VARCHAR, false, false, true, "(20)")
        .column("LAST_NAME", DataType.VARCHAR, false, true, false, "(30)")
        .build();

    assertNotNull(sql);
    assertEquals("CREATE COLUMN TABLE CUSTOMERS ( ID INTEGER NOT NULL PRIMARY KEY , FIRST_NAME VARCHAR (20) NOT NULL UNIQUE , LAST_NAME VARCHAR (30) )", sql);
  }

  /**
   * Creates the table column.
   */
  @Test
  public void createTableColumn() {
    String sql = SqlFactory.getNative(new HanaSqlDialect())
        .create()
        .columnTable("CUSTOMERS")
        .column("ID", DataType.INTEGER, true, false, false)
        .column("FIRST_NAME", DataType.VARCHAR, false, false, true, "(20)")
        .column("LAST_NAME", DataType.VARCHAR, false, true, false, "(30)")
        .build();

    assertNotNull(sql);
    assertEquals("CREATE COLUMN TABLE CUSTOMERS ( ID INTEGER NOT NULL PRIMARY KEY , FIRST_NAME VARCHAR (20) NOT NULL UNIQUE , LAST_NAME VARCHAR (30) )", sql);
  }

  /**
   * Creates the row table.
   */
  @Test
  public void createRowTable() {
    String sql = SqlFactory.getNative(new HanaSqlDialect())
        .create()
        .rowTable("CUSTOMERS")
        .column("ID", DataType.INTEGER, true, false, false)
        .column("FIRST_NAME", DataType.VARCHAR, false, false, true, "(20)")
        .column("LAST_NAME", DataType.VARCHAR, false, true, false, "(30)")
        .build();

    assertNotNull(sql);
    assertEquals("CREATE ROW TABLE CUSTOMERS ( ID INTEGER NOT NULL PRIMARY KEY , FIRST_NAME VARCHAR (20) NOT NULL UNIQUE , LAST_NAME VARCHAR (30) )", sql);
  }

  /**
   * Creates the table column type safe.
   */
  @Test
  public void createTableColumnTypeSafe() {
    String sql = SqlFactory.getNative(new HanaSqlDialect())
        .create()
        .columnTable("CUSTOMERS")
        .columnInteger("ID", true, false, false)
        .columnVarchar("FIRST_NAME", 20, false, true, true)
        .columnVarchar("LAST_NAME", 30, false, true, false)
        .build();

    assertNotNull(sql);
    assertEquals("CREATE COLUMN TABLE CUSTOMERS ( ID INTEGER NOT NULL PRIMARY KEY , FIRST_NAME VARCHAR (20) UNIQUE , LAST_NAME VARCHAR (30) )", sql);
  }

  @Test
  public void createTableWithCompositeKeyWithSetPKOnColumnLevel() {
    String sql = SqlFactory.getNative(new HanaSqlDialect())
        .create()
        .columnTable("CUSTOMERS")
        .columnInteger("ID", true, false, false)
        .columnInteger("ID2", true, false, false)
        .columnVarchar("FIRST_NAME", 20, false, true, true)
        .columnVarchar("LAST_NAME", 30, false, true, false)
        .build();

    assertNotNull(sql);
    assertEquals("CREATE COLUMN TABLE CUSTOMERS ( ID INTEGER NOT NULL , ID2 INTEGER NOT NULL , FIRST_NAME VARCHAR (20) UNIQUE , LAST_NAME VARCHAR (30) , PRIMARY KEY(ID , ID2) )", sql);
  }

  @Test
  public void createTableWithCompositeKeyWithSetPKOnConstraintLevel() {
    String sql = SqlFactory.getNative(new HanaSqlDialect())
        .create()
        .columnTable("CUSTOMERS")
        .columnInteger("ID", false, false, false)
        .columnInteger("ID2", false, false, false)
        .columnVarchar("FIRST_NAME", 20, false, true, true)
        .columnVarchar("LAST_NAME", 30, false, true, false)
        .primaryKey(new String[]{"ID", "ID2"})
        .build();

    assertNotNull(sql);
    assertEquals("CREATE COLUMN TABLE CUSTOMERS ( ID INTEGER NOT NULL , ID2 INTEGER NOT NULL , FIRST_NAME VARCHAR (20) UNIQUE , LAST_NAME VARCHAR (30) , PRIMARY KEY ( ID , ID2 ))", sql);
  }

  @Test
  public void createTableWithCompositeKeyWithSetPKOnConstraintAndColumnLevel() {
    String sql = SqlFactory.getNative(new HanaSqlDialect())
        .create()
        .columnTable("CUSTOMERS")
        .columnInteger("ID", true, false, false)
        .columnInteger("ID2", true, false, false)
        .columnVarchar("FIRST_NAME", 20, false, true, true)
        .columnVarchar("LAST_NAME", 30, false, true, false)
        .primaryKey(new String[]{"ID", "ID2"})
        .build();

    assertNotNull(sql);
    assertEquals("CREATE COLUMN TABLE CUSTOMERS ( ID INTEGER NOT NULL , ID2 INTEGER NOT NULL , FIRST_NAME VARCHAR (20) UNIQUE , LAST_NAME VARCHAR (30) , PRIMARY KEY(ID , ID2) )", sql);
  }

  @Test
  public void createTableWithSetPKOnConstraintAndColumnLevel() {
    String sql = SqlFactory.getNative(new HanaSqlDialect())
        .create()
        .columnTable("CUSTOMERS")
        .columnInteger("ID", true, false, false)
        .columnVarchar("FIRST_NAME", 20, false, true, true)
        .primaryKey(new String[]{"ID"})
        .build();

    assertNotNull(sql);
    assertEquals("CREATE COLUMN TABLE CUSTOMERS ( ID INTEGER NOT NULL PRIMARY KEY , FIRST_NAME VARCHAR (20) UNIQUE )", sql);
  }

  @Test
  public void parseTableWithoutPK() {
    String sql = SqlFactory.getNative(new HanaSqlDialect())
        .create()
        .columnTable("CUSTOMERS")
        .columnInteger("ID", false, false, false)
        .columnVarchar("FIRST_NAME", 20, false, true, true)
        .primaryKey(new String[]{})
        .build();

    assertNotNull(sql);
    assertEquals("CREATE COLUMN TABLE CUSTOMERS ( ID INTEGER NOT NULL , FIRST_NAME VARCHAR (20) UNIQUE )", sql);
  }

  /**
   * Creates the row table with indexes.
   */
  @Test
  public void createRowTableWithIndexes() {
    String sql = SqlFactory.getNative(new HanaSqlDialect())
        .create()
        .rowTable("CUSTOMERS")
        .column("ID", DataType.INTEGER, true, false, false)
        .column("FIRST_NAME", DataType.VARCHAR, false, false, true, "(20)")
        .column("LAST_NAME", DataType.VARCHAR, false, true, false, "(30)")
        .index("I1", false, "DESC", "BTREE", new HashSet<>(List.of("LAST_NAME")))
        .unique("I2", new String[] {"ID"}, "CPBTREE", "ASC")
        .build();

    assertNotNull("Unexpected result from builder", sql);
    assertTrue("Expected create table statement was not found", sql.contains("CREATE ROW TABLE CUSTOMERS ( ID INTEGER NOT NULL PRIMARY KEY , FIRST_NAME VARCHAR (20) NOT NULL UNIQUE , LAST_NAME VARCHAR (30) );"));
    assertTrue("Expected unique index statement was not found", sql.contains("CREATE UNIQUE CPBTREE INDEX I2 ON CUSTOMERS ( ID ) ASC"));
    assertTrue("Expected index statement was not found", sql.contains("CREATE BTREE INDEX I1 ON CUSTOMERS ( LAST_NAME ) DESC"));
    int expectedStatementLength = "CREATE ROW TABLE CUSTOMERS ( ID INTEGER NOT NULL PRIMARY KEY , FIRST_NAME VARCHAR (20) NOT NULL UNIQUE , LAST_NAME VARCHAR (30) ); CREATE UNIQUE CPBTREE INDEX I2 ON CUSTOMERS ( ID ) ASC; CREATE BTREE INDEX I1 ON CUSTOMERS ( LAST_NAME ) DESC".length();
    assertEquals("Unexpected length of statement", expectedStatementLength, sql.length());
  }

  @Test
  public void testCreateTableWithIndexes() {
    Table table = SqlFactory.getNative(new HanaSqlDialect())
        .create()
        .rowTable("CUSTOMERS")
        .column("ID", DataType.INTEGER, true, false, false)
        .column("FIRST_NAME", DataType.VARCHAR, false, false, true, "(20)")
        .column("LAST_NAME", DataType.VARCHAR, false, true, false, "(30)")
        .index("I1", false, "DESC", "BTREE", new HashSet<>(List.of("LAST_NAME")))
        .unique("I2", new String[] {"ID"}, "CPBTREE", "ASC")
        .buildTable();

    assertEquals("Unexpected create table statement", "CREATE ROW TABLE CUSTOMERS ( ID INTEGER NOT NULL PRIMARY KEY , FIRST_NAME VARCHAR (20) NOT NULL UNIQUE , LAST_NAME VARCHAR (30) )", table.getCreateTableStatement());

    Collection<String> expected = Arrays.asList("CREATE UNIQUE CPBTREE INDEX I2 ON CUSTOMERS ( ID ) ASC",
        "CREATE BTREE INDEX I1 ON CUSTOMERS ( LAST_NAME ) DESC");


    MatcherAssert.assertThat("Indices equality without order",
        table.getCreateIndicesStatements(), Matchers.containsInAnyOrder(expected.toArray()));
  }

}