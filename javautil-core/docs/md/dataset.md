# Dataset

javautil.org datasets are classes that can be viewed as a list of tuples with metadata.

## Creation

List of Lists
List of Maps

## Metadata

	private String                 aggregateFunction;
	private String                 columnName;
	private Integer                columnIndex;
	private Integer                columnSize;
	private Integer                columnDisplaySize;
	private String                 columnTypeName;
	private Integer                columnType;
	private String                 comments;
	private DataType               dataType;
	private String                 dataTypeName;
	private boolean                externalFlag;
	private boolean                injectedGroupField = false;
	private Boolean                nullable;
	private Boolean                notNullable;
	private Integer                precision;
	private Integer                scale;
	// 
	private Boolean                definitelyNullable;
	private Boolean                unKnownNullable;

### Labeling
	private String                 groupName;
	private String                 heading;
	private String                 label;

### Formatting 
	private SimpleDateFormat       inputDateFormat;
	private String                 inputDateFormatString;
	private String                 workbookFormula;
	private String                 javaFormat;
	private String                 excelFormat;
	private HorizontalAlignment    horizontalAlignment;

### Other
	private String                 attributeName;

# Rendering

json
xml
worksheet region
