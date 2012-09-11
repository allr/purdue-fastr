package r;

import org.junit.runner.*;
import org.junit.runners.*;
import org.junit.runners.Suite.SuiteClasses;


@RunWith(Suite.class)
@SuiteClasses({TestSimpleAssignment.class, TestSimpleFunctions.class, TestSimpleIfEvaluator.class})
public class BaseTests {

}
