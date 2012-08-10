[ "$ANTLR" ] || ANTLR=antlr-3.4

GRAM="R.g"
FILES="RLexer.java RParser.java"

echo Generate $GRAM
$ANTLR $GRAM

for f in $FILES ; do
	echo Patching $f
	echo | cat $f - | perl -pne 's/(.*getGrammarFileName\(.*)/    \@Override\n\1/' | perl -pne 's/(.* getDescription\(.*)/        \@Override\n\1/'  | perl -pne 's/(.*mTokens\(.*)/    \@Override\n\1/'| perl -pne 's/(.*getTokenNames\(.*)/    \@Override\n\1/' | perl -pne 's/(.*specialStateTransition\(.*)/        \@Override\n\1/' | perl -pne 's/\@SuppressWarnings.*/\@SuppressWarnings({"all"})/' > $f.tmp
	mv -f $f.tmp $f
done
