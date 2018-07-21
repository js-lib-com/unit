package js.unit.data;

import java.util.Random;
import java.util.TimeZone;

public final class RandomTimeZone implements RandomValue
{
  private static final Random random = new Random();

  @Override
  public Object value(int maxLength)
  {
    String[] availableIDs = TimeZone.getAvailableIDs();
    return TimeZone.getTimeZone(availableIDs[random.nextInt(availableIDs.length)]);
  }
}
