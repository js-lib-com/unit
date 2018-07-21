package js.unit.data;

import java.io.File;
import java.util.List;
import java.util.Random;

import js.unit.util.Strings;

public final class RandomFile implements RandomValue
{
  private static final int MAX_LENGTH = 128;

  private static final RandomString randomString = new RandomString();
  private static final Random randomNumber = new Random();

  @Override
  public Object value(int maxLength)
  {
    int length = maxLength != 0 ? maxLength : MAX_LENGTH;
    int pathElementsCount = 2 + randomNumber.nextInt(8);
    if(pathElementsCount == 0) pathElementsCount = 1;
    int pathElementLength = length / pathElementsCount;

    List<String> pathElements = randomString.names(pathElementsCount, pathElementLength);
    return new File(File.separator + Strings.join(pathElements, File.separator));
  }
}
