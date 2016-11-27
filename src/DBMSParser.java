/* Generated By:JavaCC: Do not edit this line. DBMSParser.java */
import java.util.ArrayList;
import java.util.Locale;

import definition.TableDefinition;
import ddl.DDL;

public class DBMSParser implements DBMSParserConstants {
  public static TableDefinition tableDefinition;
  public static String argTableName;

  public static final int PRINT_SYNTAX_ERROR = 0;
  public static final int CREATE_TABLE_REQUESTED = 1;
  public static final int DROP_TABLE_REQUESTED = 2;
  public static final int DESC_REQUESTED = 3;
  public static final int INSERT_REQUESTED = 4;
  public static final int DELETE_REQUESTED = 5;
  public static final int SELECT_REQUESTED = 6;
  public static final int SHOW_TABLES_REQUESTED = 7;

  public static void main(String args[]) throws ParseException
  {
    DBMSParser parser = new DBMSParser(System.in);
    System.out.print("DB_2015-11543> ");

    while (true) {
      try {
        parser.command();
      } catch (ParseException e) {
        executeCommand(PRINT_SYNTAX_ERROR);
        DBMSParser.ReInit(System.in);
      } catch (Exception e) {
        System.out.println("UnexpectedError");
        System.out.print("DB_2015-11543> ");
        DBMSParser.ReInit(System.in);
      }
    }
  }

  public static void executeCommand(int q)
  {
    switch(q)
    {
      case PRINT_SYNTAX_ERROR:
        System.out.println("Syntax error");
        break;
      case CREATE_TABLE_REQUESTED:
        DDL.executeCreateTable(tableDefinition);
        break;
      case DROP_TABLE_REQUESTED:
        DDL.executeDropTable(argTableName);
        break;
      case DESC_REQUESTED:
        DDL.executeDescTable(argTableName);
        break;
      case INSERT_REQUESTED:
        System.out.println("\u005c'INSERT\u005c' requested");
        break;
      case DELETE_REQUESTED:
        System.out.println("\u005c'DELETE\u005c' requested");
        break;
      case SELECT_REQUESTED:
        System.out.println("\u005c'SELECT\u005c' requested");
        break;
      case SHOW_TABLES_REQUESTED:
        DDL.executeShowTable();
        break;
    }
    System.out.print("DB_2015-11543> ");
  }

/** * basic query structure */
  static final public void command() throws ParseException {
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case CREATE:
    case DROP:
    case DESC:
    case INSERT:
    case DELETE:
    case SELECT:
    case SHOW:
      queryList();
      break;
    case EXIT:
      jj_consume_token(EXIT);
      jj_consume_token(SEMICOLON);
      System.exit(0);
      break;
    default:
      jj_la1[0] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
  }

  static final public void queryList() throws ParseException {
  int q;
    label_1:
    while (true) {
      q = query();
      jj_consume_token(SEMICOLON);
      executeCommand(q);
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case CREATE:
      case DROP:
      case DESC:
      case INSERT:
      case DELETE:
      case SELECT:
      case SHOW:
        ;
        break;
      default:
        jj_la1[1] = jj_gen;
        break label_1;
      }
    }
  }

  static final public int query() throws ParseException {
  int q;
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case CREATE:
      createTableQuery();
      q = CREATE_TABLE_REQUESTED;
      break;
    case DROP:
      dropTableQuery();
          q = DROP_TABLE_REQUESTED;
      break;
    case DESC:
      descQuery();
          q = DESC_REQUESTED;
      break;
    case SELECT:
      selectQuery();
          q = SELECT_REQUESTED;
      break;
    case INSERT:
      insertQuery();
          q = INSERT_REQUESTED;
      break;
    case DELETE:
      deleteQuery();
          q = DELETE_REQUESTED;
      break;
    case SHOW:
      showTablesQuery();
          q = SHOW_TABLES_REQUESTED;
      break;
    default:
      jj_la1[2] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
      {if (true) return q;}
    throw new Error("Missing return statement in function");
  }

/** * common components */

// table name  static final public String tableName() throws ParseException {
  Token t;
    t = jj_consume_token(LEGAL_IDENTIFIER);
    {if (true) return t.image.toLowerCase(Locale.ENGLISH);}
    throw new Error("Missing return statement in function");
  }

// column name  static final public String columnName() throws ParseException {
  Token t;
    t = jj_consume_token(LEGAL_IDENTIFIER);
    {if (true) return t.image.toLowerCase(Locale.ENGLISH);}
    throw new Error("Missing return statement in function");
  }

// column name list : (column_name1, column_name2, ...)  static final public ArrayList<String> columnNameList() throws ParseException {
  ArrayList<String> columnNameList = new ArrayList<String>();
  String str;
    jj_consume_token(LEFT_PAREN);
    str = columnName();
    columnNameList.add(str);
    label_2:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case COMMA:
        ;
        break;
      default:
        jj_la1[3] = jj_gen;
        break label_2;
      }
      jj_consume_token(COMMA);
      str = columnName();
      columnNameList.add(str);
    }
    jj_consume_token(RIGHT_PAREN);
    {if (true) return columnNameList;}
    throw new Error("Missing return statement in function");
  }

/** * CREATE TABLE query */

// create table query  static final public void createTableQuery() throws ParseException {
  String str;
    jj_consume_token(CREATE);
    jj_consume_token(TABLE);
    str = tableName();
    tableDefinition = new TableDefinition();
    tableDefinition.setTableName(str);
    tableElementList();
  }

// table element list  static final public void tableElementList() throws ParseException {
    jj_consume_token(LEFT_PAREN);
    tableElement();
    label_3:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case COMMA:
        ;
        break;
      default:
        jj_la1[4] = jj_gen;
        break label_3;
      }
      jj_consume_token(COMMA);
      tableElement();
    }
    jj_consume_token(RIGHT_PAREN);
  }

// table element  static final public void tableElement() throws ParseException {
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case LEGAL_IDENTIFIER:
      columnDefinition();
      break;
    case PRIMARY:
    case FOREIGN:
      tableConstraintDefinition();
      break;
    default:
      jj_la1[5] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
  }

// column definition  static final public void columnDefinition() throws ParseException {
  String columnName;
  String dataType;
  boolean notNullFlag = false;
    columnName = columnName();
    dataType = dataType();
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case NOT:
      jj_consume_token(NOT);
      jj_consume_token(NULL);
      notNullFlag = true;
      break;
    default:
      jj_la1[6] = jj_gen;
      ;
    }
    tableDefinition.addColumnDefinition(columnName, dataType, notNullFlag);
  }

// table constraint definition  static final public void tableConstraintDefinition() throws ParseException {
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case PRIMARY:
      primaryKeyConstraint();
      break;
    case FOREIGN:
      referentialConstraint();
      break;
    default:
      jj_la1[7] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
  }

// primary key constraint  static final public void primaryKeyConstraint() throws ParseException {
  ArrayList<String> primaryKeyList;
    jj_consume_token(PRIMARY);
    jj_consume_token(KEY);
    primaryKeyList = columnNameList();
    tableDefinition.setPrimaryKeyDefinition(primaryKeyList);
  }

// referential constraint  static final public void referentialConstraint() throws ParseException {
  String referencedTableName;
  ArrayList<String> referencingColumnNames;
  ArrayList<String> referencedColumnNames;
    jj_consume_token(FOREIGN);
    jj_consume_token(KEY);
    referencingColumnNames = columnNameList();
    jj_consume_token(REFERENCES);
    referencedTableName = tableName();
    referencedColumnNames = columnNameList();
    tableDefinition.addForeignKeydefinition(referencedTableName, referencingColumnNames, referencedColumnNames);
  }

// data types  static final public String dataType() throws ParseException {
  Token t;
  String str;
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case INT:
      jj_consume_token(INT);
      str = "int";
      break;
    case CHAR:
      jj_consume_token(CHAR);
        str = "char";
      jj_consume_token(LEFT_PAREN);
        str += "(";
      t = jj_consume_token(INT_VALUE);
        str += t.image;
      jj_consume_token(RIGHT_PAREN);
        str += ")";
      break;
    case DATE:
      jj_consume_token(DATE);
          str = "date";
      break;
    default:
      jj_la1[8] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
    {if (true) return str;}
    throw new Error("Missing return statement in function");
  }

/** * DROP TABLE query */

// drop table query  static final public void dropTableQuery() throws ParseException {
    jj_consume_token(DROP);
    jj_consume_token(TABLE);
    argTableName = tableName();
  }

/** * DESC query */

// desc query  static final public void descQuery() throws ParseException {
    jj_consume_token(DESC);
    argTableName = tableName();
  }

/** * SELECT query */

// select query  static final public void selectQuery() throws ParseException {
    jj_consume_token(SELECT);
    selectList();
    tableExpression();
  }

// select list  static final public void selectList() throws ParseException {
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case ASTERISK:
      jj_consume_token(ASTERISK);
      break;
    case LEGAL_IDENTIFIER:
      selectedColumn();
      label_4:
      while (true) {
        switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
        case COMMA:
          ;
          break;
        default:
          jj_la1[9] = jj_gen;
          break label_4;
        }
        jj_consume_token(COMMA);
        selectedColumn();
      }
      break;
    default:
      jj_la1[10] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
  }

// selected column  static final public void selectedColumn() throws ParseException {
    if (jj_2_1(2)) {
      tableName();
      jj_consume_token(PERIOD);
    } else {
      ;
    }
    columnName();
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case AS:
      jj_consume_token(AS);
      columnName();
      break;
    default:
      jj_la1[11] = jj_gen;
      ;
    }
  }

// table expression  static final public void tableExpression() throws ParseException {
    fromClause();
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case WHERE:
      whereClause();
      break;
    default:
      jj_la1[12] = jj_gen;
      ;
    }
  }

// from clause  static final public void fromClause() throws ParseException {
    jj_consume_token(FROM);
    tableReferenceList();
  }

// table reference list  static final public void tableReferenceList() throws ParseException {
    referedTable();
    label_5:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case COMMA:
        ;
        break;
      default:
        jj_la1[13] = jj_gen;
        break label_5;
      }
      jj_consume_token(COMMA);
      referedTable();
    }
  }

// refered table  static final public void referedTable() throws ParseException {
    tableName();
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case AS:
      jj_consume_token(AS);
      tableName();
      break;
    default:
      jj_la1[14] = jj_gen;
      ;
    }
  }

// where clause  static final public void whereClause() throws ParseException {
    jj_consume_token(WHERE);
    booleanValueExpression();
  }

// boolean value expression  static final public void booleanValueExpression() throws ParseException {
    booleanTerm();
    label_6:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case OR:
        ;
        break;
      default:
        jj_la1[15] = jj_gen;
        break label_6;
      }
      jj_consume_token(OR);
      booleanTerm();
    }
  }

// boolean term  static final public void booleanTerm() throws ParseException {
    booleanFactor();
    label_7:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case AND:
        ;
        break;
      default:
        jj_la1[16] = jj_gen;
        break label_7;
      }
      jj_consume_token(AND);
      booleanFactor();
    }
  }

// boolean factor  static final public void booleanFactor() throws ParseException {
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case NOT:
      jj_consume_token(NOT);
      break;
    default:
      jj_la1[17] = jj_gen;
      ;
    }
    booleanTest();
  }

// boolean test  static final public void booleanTest() throws ParseException {
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case INT_VALUE:
    case CHAR_STRING:
    case DATE_VALUE:
    case LEGAL_IDENTIFIER:
      predicate();
      break;
    case LEFT_PAREN:
      parenthesizedBooleanExpression();
      break;
    default:
      jj_la1[18] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
  }

// parenthesized boolean expression  static final public void parenthesizedBooleanExpression() throws ParseException {
    jj_consume_token(LEFT_PAREN);
    booleanValueExpression();
    jj_consume_token(RIGHT_PAREN);
  }

// predicate  static final public void predicate() throws ParseException {
    if (jj_2_2(4)) {
      comparisonPredicate();
    } else {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case LEGAL_IDENTIFIER:
        nullPredicate();
        break;
      default:
        jj_la1[19] = jj_gen;
        jj_consume_token(-1);
        throw new ParseException();
      }
    }
  }

// comparison predicate  static final public void comparisonPredicate() throws ParseException {
    compOperand();
    compOp();
    compOperand();
  }

// comparison operand  static final public void compOperand() throws ParseException {
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case INT_VALUE:
    case CHAR_STRING:
    case DATE_VALUE:
      comparableValue();
      break;
    case LEGAL_IDENTIFIER:
      if (jj_2_3(2)) {
        tableName();
        jj_consume_token(PERIOD);
      } else {
        ;
      }
      columnName();
      break;
    default:
      jj_la1[20] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
  }

// comparison operator  static final public void compOp() throws ParseException {
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case EQUAL_TO:
      jj_consume_token(EQUAL_TO);
      break;
    case NOT_EQUAL:
      jj_consume_token(NOT_EQUAL);
      break;
    case LESS_THAN:
      jj_consume_token(LESS_THAN);
      break;
    case GREATER_THAN:
      jj_consume_token(GREATER_THAN);
      break;
    case GREATER_THAN_OR_EQUAL_TO:
      jj_consume_token(GREATER_THAN_OR_EQUAL_TO);
      break;
    case LESS_THAN_OR_EQUAL_TO:
      jj_consume_token(LESS_THAN_OR_EQUAL_TO);
      break;
    default:
      jj_la1[21] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
  }

// comparable value  static final public void comparableValue() throws ParseException {
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case INT_VALUE:
      jj_consume_token(INT_VALUE);
      break;
    case CHAR_STRING:
      jj_consume_token(CHAR_STRING);
      break;
    case DATE_VALUE:
      jj_consume_token(DATE_VALUE);
      break;
    default:
      jj_la1[22] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
  }

// null predicate  static final public void nullPredicate() throws ParseException {
    if (jj_2_4(2)) {
      tableName();
      jj_consume_token(PERIOD);
    } else {
      ;
    }
    columnName();
    nullOperation();
  }

// null operation  static final public void nullOperation() throws ParseException {
    jj_consume_token(IS);
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case NOT:
      jj_consume_token(NOT);
      break;
    default:
      jj_la1[23] = jj_gen;
      ;
    }
    jj_consume_token(NULL);
  }

/** * INSERT query */

// insert query  static final public void insertQuery() throws ParseException {
    jj_consume_token(INSERT);
    jj_consume_token(INTO);
    tableName();
    insertColumnsAndSource();
  }

// insert columns and source  static final public void insertColumnsAndSource() throws ParseException {
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case LEFT_PAREN:
      columnNameList();
      break;
    default:
      jj_la1[24] = jj_gen;
      ;
    }
    valueList();
  }

// value list  static final public void valueList() throws ParseException {
    jj_consume_token(VALUES);
    jj_consume_token(LEFT_PAREN);
    value();
    label_8:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case COMMA:
        ;
        break;
      default:
        jj_la1[25] = jj_gen;
        break label_8;
      }
      jj_consume_token(COMMA);
      value();
    }
    jj_consume_token(RIGHT_PAREN);
  }

// value  static final public void value() throws ParseException {
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case NULL:
      jj_consume_token(NULL);
      break;
    case INT_VALUE:
    case CHAR_STRING:
    case DATE_VALUE:
      comparableValue();
      break;
    default:
      jj_la1[26] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
  }

/** * DELETE query */

// delete query  static final public void deleteQuery() throws ParseException {
    jj_consume_token(DELETE);
    jj_consume_token(FROM);
    tableName();
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case WHERE:
      whereClause();
      break;
    default:
      jj_la1[27] = jj_gen;
      ;
    }
  }

/** * SHOW TABLE query */

// show table query  static final public void showTablesQuery() throws ParseException {
    jj_consume_token(SHOW);
    jj_consume_token(TABLES);
  }

  static private boolean jj_2_1(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return !jj_3_1(); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(0, xla); }
  }

  static private boolean jj_2_2(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return !jj_3_2(); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(1, xla); }
  }

  static private boolean jj_2_3(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return !jj_3_3(); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(2, xla); }
  }

  static private boolean jj_2_4(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return !jj_3_4(); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(3, xla); }
  }

  static private boolean jj_3R_12() {
    Token xsp;
    xsp = jj_scanpos;
    if (jj_scan_token(40)) {
    jj_scanpos = xsp;
    if (jj_scan_token(41)) {
    jj_scanpos = xsp;
    if (jj_scan_token(42)) {
    jj_scanpos = xsp;
    if (jj_scan_token(43)) {
    jj_scanpos = xsp;
    if (jj_scan_token(44)) {
    jj_scanpos = xsp;
    if (jj_scan_token(45)) return true;
    }
    }
    }
    }
    }
    return false;
  }

  static private boolean jj_3R_10() {
    if (jj_3R_11()) return true;
    if (jj_3R_12()) return true;
    if (jj_3R_11()) return true;
    return false;
  }

  static private boolean jj_3_3() {
    if (jj_3R_9()) return true;
    if (jj_scan_token(PERIOD)) return true;
    return false;
  }

  static private boolean jj_3R_9() {
    if (jj_scan_token(LEGAL_IDENTIFIER)) return true;
    return false;
  }

  static private boolean jj_3_1() {
    if (jj_3R_9()) return true;
    if (jj_scan_token(PERIOD)) return true;
    return false;
  }

  static private boolean jj_3_2() {
    if (jj_3R_10()) return true;
    return false;
  }

  static private boolean jj_3R_15() {
    Token xsp;
    xsp = jj_scanpos;
    if (jj_scan_token(46)) {
    jj_scanpos = xsp;
    if (jj_scan_token(47)) {
    jj_scanpos = xsp;
    if (jj_scan_token(48)) return true;
    }
    }
    return false;
  }

  static private boolean jj_3R_14() {
    Token xsp;
    xsp = jj_scanpos;
    if (jj_3_3()) jj_scanpos = xsp;
    if (jj_3R_16()) return true;
    return false;
  }

  static private boolean jj_3R_13() {
    if (jj_3R_15()) return true;
    return false;
  }

  static private boolean jj_3R_11() {
    Token xsp;
    xsp = jj_scanpos;
    if (jj_3R_13()) {
    jj_scanpos = xsp;
    if (jj_3R_14()) return true;
    }
    return false;
  }

  static private boolean jj_3R_16() {
    if (jj_scan_token(LEGAL_IDENTIFIER)) return true;
    return false;
  }

  static private boolean jj_3_4() {
    if (jj_3R_9()) return true;
    if (jj_scan_token(PERIOD)) return true;
    return false;
  }

  static private boolean jj_initialized_once = false;
  /** Generated Token Manager. */
  static public DBMSParserTokenManager token_source;
  static SimpleCharStream jj_input_stream;
  /** Current token. */
  static public Token token;
  /** Next token. */
  static public Token jj_nt;
  static private int jj_ntk;
  static private Token jj_scanpos, jj_lastpos;
  static private int jj_la;
  static private int jj_gen;
  static final private int[] jj_la1 = new int[28];
  static private int[] jj_la1_0;
  static private int[] jj_la1_1;
  static {
      jj_la1_init_0();
      jj_la1_init_1();
   }
   private static void jj_la1_init_0() {
      jj_la1_0 = new int[] {0x6ba20,0x6ba00,0x6ba00,0x0,0x0,0xa00000,0x20000000,0xa00000,0x1c0,0x0,0x0,0x2000000,0x4000000,0x0,0x2000000,0x8000000,0x10000000,0x20000000,0x0,0x0,0x0,0x0,0x0,0x20000000,0x0,0x0,0x100000,0x4000000,};
   }
   private static void jj_la1_init_1() {
      jj_la1_1 = new int[] {0x0,0x0,0x0,0x10,0x10,0x20000,0x0,0x0,0x0,0x10,0x20040,0x0,0x0,0x10,0x0,0x0,0x0,0x0,0x3c002,0x20000,0x3c000,0x3f00,0x1c000,0x0,0x2,0x10,0x1c000,0x0,};
   }
  static final private JJCalls[] jj_2_rtns = new JJCalls[4];
  static private boolean jj_rescan = false;
  static private int jj_gc = 0;

  /** Constructor with InputStream. */
  public DBMSParser(java.io.InputStream stream) {
     this(stream, null);
  }
  /** Constructor with InputStream and supplied encoding */
  public DBMSParser(java.io.InputStream stream, String encoding) {
    if (jj_initialized_once) {
      System.out.println("ERROR: Second call to constructor of static parser.  ");
      System.out.println("       You must either use ReInit() or set the JavaCC option STATIC to false");
      System.out.println("       during parser generation.");
      throw new Error();
    }
    jj_initialized_once = true;
    try { jj_input_stream = new SimpleCharStream(stream, encoding, 1, 1); } catch(java.io.UnsupportedEncodingException e) { throw new RuntimeException(e); }
    token_source = new DBMSParserTokenManager(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 28; i++) jj_la1[i] = -1;
    for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
  }

  /** Reinitialise. */
  static public void ReInit(java.io.InputStream stream) {
     ReInit(stream, null);
  }
  /** Reinitialise. */
  static public void ReInit(java.io.InputStream stream, String encoding) {
    try { jj_input_stream.ReInit(stream, encoding, 1, 1); } catch(java.io.UnsupportedEncodingException e) { throw new RuntimeException(e); }
    token_source.ReInit(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 28; i++) jj_la1[i] = -1;
    for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
  }

  /** Constructor. */
  public DBMSParser(java.io.Reader stream) {
    if (jj_initialized_once) {
      System.out.println("ERROR: Second call to constructor of static parser. ");
      System.out.println("       You must either use ReInit() or set the JavaCC option STATIC to false");
      System.out.println("       during parser generation.");
      throw new Error();
    }
    jj_initialized_once = true;
    jj_input_stream = new SimpleCharStream(stream, 1, 1);
    token_source = new DBMSParserTokenManager(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 28; i++) jj_la1[i] = -1;
    for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
  }

  /** Reinitialise. */
  static public void ReInit(java.io.Reader stream) {
    jj_input_stream.ReInit(stream, 1, 1);
    token_source.ReInit(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 28; i++) jj_la1[i] = -1;
    for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
  }

  /** Constructor with generated Token Manager. */
  public DBMSParser(DBMSParserTokenManager tm) {
    if (jj_initialized_once) {
      System.out.println("ERROR: Second call to constructor of static parser. ");
      System.out.println("       You must either use ReInit() or set the JavaCC option STATIC to false");
      System.out.println("       during parser generation.");
      throw new Error();
    }
    jj_initialized_once = true;
    token_source = tm;
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 28; i++) jj_la1[i] = -1;
    for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
  }

  /** Reinitialise. */
  public void ReInit(DBMSParserTokenManager tm) {
    token_source = tm;
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 28; i++) jj_la1[i] = -1;
    for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
  }

  static private Token jj_consume_token(int kind) throws ParseException {
    Token oldToken;
    if ((oldToken = token).next != null) token = token.next;
    else token = token.next = token_source.getNextToken();
    jj_ntk = -1;
    if (token.kind == kind) {
      jj_gen++;
      if (++jj_gc > 100) {
        jj_gc = 0;
        for (int i = 0; i < jj_2_rtns.length; i++) {
          JJCalls c = jj_2_rtns[i];
          while (c != null) {
            if (c.gen < jj_gen) c.first = null;
            c = c.next;
          }
        }
      }
      return token;
    }
    token = oldToken;
    jj_kind = kind;
    throw generateParseException();
  }

  static private final class LookaheadSuccess extends java.lang.Error { }
  static final private LookaheadSuccess jj_ls = new LookaheadSuccess();
  static private boolean jj_scan_token(int kind) {
    if (jj_scanpos == jj_lastpos) {
      jj_la--;
      if (jj_scanpos.next == null) {
        jj_lastpos = jj_scanpos = jj_scanpos.next = token_source.getNextToken();
      } else {
        jj_lastpos = jj_scanpos = jj_scanpos.next;
      }
    } else {
      jj_scanpos = jj_scanpos.next;
    }
    if (jj_rescan) {
      int i = 0; Token tok = token;
      while (tok != null && tok != jj_scanpos) { i++; tok = tok.next; }
      if (tok != null) jj_add_error_token(kind, i);
    }
    if (jj_scanpos.kind != kind) return true;
    if (jj_la == 0 && jj_scanpos == jj_lastpos) throw jj_ls;
    return false;
  }


/** Get the next Token. */
  static final public Token getNextToken() {
    if (token.next != null) token = token.next;
    else token = token.next = token_source.getNextToken();
    jj_ntk = -1;
    jj_gen++;
    return token;
  }

/** Get the specific Token. */
  static final public Token getToken(int index) {
    Token t = token;
    for (int i = 0; i < index; i++) {
      if (t.next != null) t = t.next;
      else t = t.next = token_source.getNextToken();
    }
    return t;
  }

  static private int jj_ntk() {
    if ((jj_nt=token.next) == null)
      return (jj_ntk = (token.next=token_source.getNextToken()).kind);
    else
      return (jj_ntk = jj_nt.kind);
  }

  static private java.util.List<int[]> jj_expentries = new java.util.ArrayList<int[]>();
  static private int[] jj_expentry;
  static private int jj_kind = -1;
  static private int[] jj_lasttokens = new int[100];
  static private int jj_endpos;

  static private void jj_add_error_token(int kind, int pos) {
    if (pos >= 100) return;
    if (pos == jj_endpos + 1) {
      jj_lasttokens[jj_endpos++] = kind;
    } else if (jj_endpos != 0) {
      jj_expentry = new int[jj_endpos];
      for (int i = 0; i < jj_endpos; i++) {
        jj_expentry[i] = jj_lasttokens[i];
      }
      jj_entries_loop: for (java.util.Iterator<?> it = jj_expentries.iterator(); it.hasNext();) {
        int[] oldentry = (int[])(it.next());
        if (oldentry.length == jj_expentry.length) {
          for (int i = 0; i < jj_expentry.length; i++) {
            if (oldentry[i] != jj_expentry[i]) {
              continue jj_entries_loop;
            }
          }
          jj_expentries.add(jj_expentry);
          break jj_entries_loop;
        }
      }
      if (pos != 0) jj_lasttokens[(jj_endpos = pos) - 1] = kind;
    }
  }

  /** Generate ParseException. */
  static public ParseException generateParseException() {
    jj_expentries.clear();
    boolean[] la1tokens = new boolean[58];
    if (jj_kind >= 0) {
      la1tokens[jj_kind] = true;
      jj_kind = -1;
    }
    for (int i = 0; i < 28; i++) {
      if (jj_la1[i] == jj_gen) {
        for (int j = 0; j < 32; j++) {
          if ((jj_la1_0[i] & (1<<j)) != 0) {
            la1tokens[j] = true;
          }
          if ((jj_la1_1[i] & (1<<j)) != 0) {
            la1tokens[32+j] = true;
          }
        }
      }
    }
    for (int i = 0; i < 58; i++) {
      if (la1tokens[i]) {
        jj_expentry = new int[1];
        jj_expentry[0] = i;
        jj_expentries.add(jj_expentry);
      }
    }
    jj_endpos = 0;
    jj_rescan_token();
    jj_add_error_token(0, 0);
    int[][] exptokseq = new int[jj_expentries.size()][];
    for (int i = 0; i < jj_expentries.size(); i++) {
      exptokseq[i] = jj_expentries.get(i);
    }
    return new ParseException(token, exptokseq, tokenImage);
  }

  /** Enable tracing. */
  static final public void enable_tracing() {
  }

  /** Disable tracing. */
  static final public void disable_tracing() {
  }

  static private void jj_rescan_token() {
    jj_rescan = true;
    for (int i = 0; i < 4; i++) {
    try {
      JJCalls p = jj_2_rtns[i];
      do {
        if (p.gen > jj_gen) {
          jj_la = p.arg; jj_lastpos = jj_scanpos = p.first;
          switch (i) {
            case 0: jj_3_1(); break;
            case 1: jj_3_2(); break;
            case 2: jj_3_3(); break;
            case 3: jj_3_4(); break;
          }
        }
        p = p.next;
      } while (p != null);
      } catch(LookaheadSuccess ls) { }
    }
    jj_rescan = false;
  }

  static private void jj_save(int index, int xla) {
    JJCalls p = jj_2_rtns[index];
    while (p.gen > jj_gen) {
      if (p.next == null) { p = p.next = new JJCalls(); break; }
      p = p.next;
    }
    p.gen = jj_gen + xla - jj_la; p.first = token; p.arg = xla;
  }

  static final class JJCalls {
    int gen;
    Token first;
    int arg;
    JJCalls next;
  }

}
