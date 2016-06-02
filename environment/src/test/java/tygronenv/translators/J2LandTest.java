package tygronenv.translators;

import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.vividsolutions.jts.geom.MultiPolygon;

import eis.eis2java.exception.TranslationException;
import eis.eis2java.translation.Translator;
import eis.iilang.Function;
import nl.tytech.core.client.event.EventManager;
import nl.tytech.core.net.serializable.MapLink;
import nl.tytech.core.structure.ItemMap;
import nl.tytech.data.engine.item.Land;
import nl.tytech.data.engine.item.Zone;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.LinkedList;

/**
 * Class which contains tests for the J2Land.java class.
 * @author Nick Cleintuar (DaNSHaL).
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({EventManager.class, Land.class, Zone.class})
public class J2LandTest {

	/**
     * Translator for the Land class.
	 */
	private J2Land translator;

	/**
	 * Field for object Land which is used as mock.
	 */
	private Land land;

	/**
	 * Field for object Zone which is used as mock.
	 */
	private Zone zone;

	/**
	 * Initialization method called before every test.
	 */
	@SuppressWarnings("unchecked")
	@Before
	public void init() {
		translator = new J2Land();
		land = PowerMockito.mock(Land.class);
		zone = PowerMockito.mock(Zone.class);
		MultiPolygon mp = mock(MultiPolygon.class);
		J2MultiPolygon j2mp = new J2MultiPolygon();
		Translator.getInstance().registerJava2ParameterTranslator(j2mp);
		ItemMap<Zone> map = (ItemMap<Zone>) mock(ItemMap.class);
		LinkedList<Zone> list = new LinkedList<Zone>();
		when(map.values()).thenReturn(list);
		PowerMockito.mockStatic(EventManager.class);
		PowerMockito.when(EventManager.<Zone>getItemMap(MapLink.ZONES)).thenReturn(map);
		PowerMockito.when(land.getMultiPolygon()).thenReturn(mp);
		PowerMockito.when(zone.getMultiPolygon()).thenReturn(mp);
		PowerMockito.when(land.getOwnerID()).thenReturn(0);
		PowerMockito.when(land.getID()).thenReturn(0);
		PowerMockito.when(zone.getID()).thenReturn(0);
	}

	/**
	 * Test method which verifies that methods that are called.
	 * @throws TranslationException
	 *             thrown if translating fails.
	 */
	@Test
	public void testTranslate() throws TranslationException {
		Function function = (Function) translator.translate(land)[0];
		verify(land, atLeast(1)).getOwnerID();
		verify(land, atLeast(1)).getMultiPolygon();
		verify(land, atLeast(1)).getID();
	}
}
