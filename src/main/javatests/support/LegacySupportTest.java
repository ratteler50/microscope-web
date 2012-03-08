package support;

import static com.google.common.truth.Truth.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class LegacySupportTest {

  @Test
  public void copyLegacies() throws Exception {
    assertThat(LegacySupport.copyLegacies(0, 0)).isEmpty();
  }

  @Test
  public void copyAndAddLegacy() throws Exception {}

  @Test
  public void getRoundsLegacies() throws Exception {}

  @Test
  public void takeDiscarded() throws Exception {}

  @Test
  public void isAuction() throws Exception {}

  @Test
  public void endAuction() throws Exception {}

  @Test
  public void needsRoundLegacies() throws Exception {}

  @Test
  public void userCanPass() throws Exception {}
}
