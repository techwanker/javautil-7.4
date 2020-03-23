ignore() {
   file=$1
   echo mv $file ${file}.ignore
}

unignore() {
   file=$1
   destname=`basename $file .ignore`
   dirname=`dirname $file`
   echo unignore mv $file $dirname/$destname
   #mv $file $dirname/$destname
}
ignore ./javautil/csv/CSVTokenizerTest.java.ignore
ignore ./javautil/csv/TabTokenizerTest.java.ignore
ignore ./javautil/document/BreakHelperTest.java.ignore
ignore ./javautil/document/layout/IncrementalDocumentRendererTest.java.ignore
ignore ./javautil/document/style/StyleDefinitionsTest.java.ignore
ignore ./javautil/document/style/SimpleBorderTest.java.ignore
ignore ./javautil/document/style/HorizontalAlignmentTest.java.ignore
ignore ./javautil/document/style/StyleParserImplTest.java.ignore
ignore ./javautil/document/style/ColorUtilTest.java.ignore
unignore ./javautil/document/style/StyleImplTest.java.ignore
unignore ./javautil/SpringRunnerTest.java.ignore
unignore ./javautil/core/CommandLineTokenizerTest.java.ignore
#exit
unignore ./javautil/core/AppTest.java.ignore
unignore ./javautil/util/ConfigurableToStringTest.java.ignore
unignore ./javautil/util/ImmutableDateTest.java.ignore
unignore ./javautil/util/MapSetTest.java.ignore
#exit
unignore ./javautil/util/DateFactoryTest.java.ignore
unignore ./javautil/util/TestBase64Coder.java.ignore
unignore ./javautil/util/ReflectUtilTest.java.ignore
unignore ./javautil/util/StaticData.java.ignore
unignore ./javautil/util/DateHelperTest.java.ignore
#exit
unignore ./javautil/util/DayTest.java.ignore
unignore ./javautil/util/ConfigurableToStringTestData.java.ignore
unignore ./javautil/util/DateFormatTest.java.ignore
unignore ./javautil/util/MathUtilTest.java.ignore
#exit
unignore ./javautil/dataset/csv/DatasetMarshallerWithMetadataTest.java.ignore
unignore ./javautil/dataset/csv/DatasetMetadataCsvUnmarshallerTest.java.ignore
unignore ./javautil/dataset/csv/DatasetCsvMetadataMarshallerTest.java.ignore
unignore ./javautil/dataset/csv/DatasetCsvUnmarshallerTest.java.ignore
unignore ./javautil/dataset/DataSet2Test.java.ignore
unignore ./javautil/dataset/MatrixModificationTest.java.ignore
unignore ./javautil/dataset/ExtendedSalesDataset.java.ignore
unignore ./javautil/dataset/DatasetMarshallerTest.java.ignore
unignore ./javautil/dataset/ColumnMetadataGrouperTest.java.ignore
unignore ./javautil/dataset/ColumnMetadataTest.java.ignore
unignore ./javautil/dataset/ColumnMetaMapTest.java.ignore
unignore ./javautil/dataset/MatrixDatasetTest.java.ignore
unignore ./javautil/dataset/ColumnMetadataCsvMarshallerTest.java.ignore
unignore ./javautil/dataset/DistinctTest.java.ignore
unignore ./javautil/dataset/testdata/IncomparableTrailingNullsDataset.java.ignore
unignore ./javautil/dataset/testdata/FeesDataset.java.ignore
unignore ./javautil/dataset/testdata/TicketsDataset.java.ignore
unignore ./javautil/dataset/ResultValidator.java.ignore
unignore ./javautil/dataset/testdata/TrailingNullsDataset.java.ignore
unignore ./javautil/dataset/testdata/ConvictionsDataset.java.ignore
#exit
unignore ./javautil/dataset/MatrixModificationNoSortTest.java.ignore
unignore ./javautil/dataset/MatrixModificationSortTest.java.ignore
unignore ./javautil/dataset/ListOfMapsDatasetTest.java.ignore
unignore ./javautil/dataset/GroupByTest.java.ignore
unignore ./javautil/dataset/DatasetSource.java.ignore
unignore ./javautil/dataset/CrosstabDatasetTest.java.ignore
unignore ./javautil/dataset/render/HtmlTableDatasetRendererTest.java.ignore
unignore ./javautil/dataset/render/AbstractTableRendererTest.java.ignore
unignore ./javautil/dataset/render/MootoolsTableRendererTest.java.ignore
unignore ./javautil/dataset/BaseTest.java.ignore
unignore ./javautil/dataset/MappedDatasetRowTest.java.ignore
unignore ./javautil/reflect/SpringFieldHelperTest.java.ignore
unignore ./javautil/reflect/Class1.java.ignore
unignore ./javautil/reflect/FieldHelperTest.java.ignore
unignore ./javautil/properties/IntegerPropertyHelperTest.java.ignore
unignore ./javautil/properties/BooleanPropertyHelperTest.java.ignore
unignore ./javautil/lang/ThreadHelperTest.java.ignore
unignore ./javautil/lang/ExceptionListExceptionTest.java.ignore
unignore ./javautil/lang/reflect/TestBean.java.ignore
unignore ./javautil/lang/reflect/ClassCacheEntryTest.java.ignore
unignore ./javautil/lang/BigDecimalCoercerTest.java.ignore
unignore ./javautil/lang/ArrayHelperTest.java.ignore
unignore ./javautil/security/CryptoTest.java.ignore
unignore ./javautil/security/PasswordHasherTest.java.ignore
unignore ./javautil/data/testdata/TestData.java.ignore
unignore ./javautil/JavaFileTest.java.ignore
unignore ./javautil/file/FileComparatorTest.java.ignore
unignore ./javautil/file/InputStreamComparatorTest.java.ignore
unignore ./javautil/file/ArchiveTest.java.ignore
unignore ./javautil/test/StackTraceUtilsTest.java.ignore
unignore ./javautil/mp3/WordExtractorImplTest.java.ignore
unignore ./javautil/xml/XmlRendererTest.java.ignore
unignore ./javautil/recordvalidation/fieldtype/DateFieldTypeTest.java.ignore
unignore ./javautil/recordvalidation/UnformattedSignedIntegerFieldTypeTest.java.ignore
unignore ./javautil/recordvalidation/FieldTypeTest.java.ignore
unignore ./javautil/io/ResourceHelperTest.java.ignore
unignore ./javautil/io/ResourceTextTest.java.ignore
unignore ./javautil/io/StringFileWriterTest.java.ignore
unignore ./javautil/io/FileSystemResourceResolverTest.java.ignore
unignore ./javautil/io/ClassPathResourceResolverTest.java.ignore
unignore ./javautil/io/IOUtilsTest.java.ignore
unignore ./javautil/io/FiltePathHelperTest.java.ignore
unignore ./javautil/io/ArchiveFileUtilsTest.java.ignore
unignore ./javautil/text/WordFrequencyTest.java.ignore
unignore ./javautil/text/TabExpanderTest.java.ignore
unignore ./javautil/text/SimpleDateFormatterTest.java.ignore
unignore ./javautil/text/CsvResourceReaderTest.java.ignore
unignore ./javautil/text/StringUtilTest.java.ignore
unignore ./javautil/text/AsStringTest.java.ignore
unignore ./javautil/text/StringHelperTest.java.ignore
unignore ./javautil/text/CSVWriterTest.java.ignore
unignore ./javautil/text/MultlineStringBuilderTest.java.ignore
unignore ./javautil/collections/ArrayComparatorTest.java.ignore
unignore ./javautil/collections/SimpleTreeNodeList.java.ignore
unignore ./javautil/collections/TreeBuilderTest.java.ignore
unignore ./javautil/collections/TreeHelperTest.java.ignore
unignore ./javautil/collections/SimpleTreeNode.java.ignore
unignore ./javautil/collections/NullCompareTest.java.ignore
unignore ./javautil/collections/ForestBuilderTest.java.ignore
unignore ./javautil/collections/TupleTest.java.ignore
unignore ./javautil/field/object/DateObjectDefinitionTest.java.ignore
