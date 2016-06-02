package tygronenv.translators;

import eis.eis2java.exception.TranslationException;
import eis.eis2java.translation.Java2Parameter;
import eis.eis2java.translation.Translator;
import eis.iilang.Function;
import eis.iilang.Numeral;
import eis.iilang.Parameter;
import eis.iilang.ParameterList;
import nl.tytech.core.client.event.EventManager;
import nl.tytech.core.net.serializable.MapLink;
import nl.tytech.core.structure.ItemMap;
import nl.tytech.data.engine.item.Land;
import nl.tytech.data.engine.item.Zone;

/**
 * Translate {@link Land} into land(ID,OwnerID,MultiPolygon,ZonesList).
 * @author W.Pasman & Nick Cleintuar & Diony Tadema (DaNSHaL).
 *
 */
public class J2Land implements Java2Parameter<Land> {

	private final Translator translator = Translator.getInstance();

	@Override
	public Parameter[] translate(final Land land) throws TranslationException {
		ParameterList parList = new ParameterList();
		ItemMap<Zone> zones = EventManager.<Zone>getItemMap(MapLink.ZONES);
		for (Zone zone : zones.values()) {
			if (zone.getMultiPolygon().intersects(land.getMultiPolygon())) {
				parList.add(new Numeral(zone.getID()));
			}
		}
		return new Parameter[] {
				new Function("land", new Numeral(land.getID()), new Numeral(land.getOwnerID()),
						translator.translate2Parameter(land.getMultiPolygon())[0], parList) };
	}

	@Override
	public Class<? extends Land> translatesFrom() {
		return Land.class;
	}

}
