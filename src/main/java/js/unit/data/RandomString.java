package js.unit.data;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public final class RandomString implements RandomValue
{
  private static int MAX_SIZE = 45;

  private static final String chars = "abcdefghijklmnoprstuvxzywABCDEFGHIJKLMNOPRSTUVXZYW0123456789ăîâşţĂÎÂŞŢαβψδεφγηιξκλμνοπρστθωχζΑΒΨΔΕΦΓΗΙΞΚΛΜΝΟΠΡΣΤΩ";
  private static final String namesChars = "abcdefghijklmnoprstuvxzyw0123456789-";
  private static final Random random = new Random();

  public RandomString()
  {
  }

  public Object value(int maxLength)
  {
    return value(chars, 1, maxLength);
  }

  public Object value(int minLength, int maxLength)
  {
    return value(chars, minLength, maxLength);
  }

  public String name(int maxLength)
  {
    return value(namesChars, 3, maxLength);
  }

  public List<String> names(int arraySize, int maxLength)
  {
    List<String> names = new ArrayList<String>(arraySize);
    while(arraySize-- > 0) {
      names.add((String)value(namesChars, 3, maxLength));
    }
    return names;
  }

  private String value(String source, int minLength, int maxLength)
  {
    int maxSize = (maxLength != 0 ? maxLength : MAX_SIZE) - minLength;
    if(maxSize <= 0) {
      maxSize = 1;
      minLength = maxLength - maxSize;
    }
    int size = minLength + random.nextInt(maxSize);
    StringBuilder sb = new StringBuilder();
    while(size-- > 0) {
      sb.append(source.charAt(random.nextInt(source.length())));
    }
    return sb.toString();
  }
}
