package tygronenv.translators;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.vividsolutions.jts.geom.MultiPolygon;

import static org.mockito.Mockito.when;

import java.util.LinkedList;
import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.mock;

import eis.eis2java.exception.TranslationException;
import nl.tytech.core.client.event.EventManager;
import nl.tytech.core.net.serializable.MapLink;
import nl.tytech.core.structure.ItemMap;
import nl.tytech.data.core.item.Item;
import nl.tytech.data.engine.item.ActionLog;
import nl.tytech.data.engine.item.PopupData;
import nl.tytech.data.engine.item.SpecialOption;
import nl.tytech.data.engine.item.SpecialOption.Type;

@RunWith(PowerMockRunner.class)
@PrepareForTest({EventManager.class})
public class J2PopupDataTest {
    
    private J2PopupData translator;
    
    private PopupData popup;
    
    @Before
    public void init() {
        translator = new J2PopupData();
        popup = PowerMockito.mock(PopupData.class);
        PowerMockito.mockStatic(EventManager.class);
        
        when(popup.getContentLinkID()).thenReturn(0);
        SpecialOption specialOption = PowerMockito.mock(SpecialOption.class);
        Type type = PowerMockito.mock(Type.class);
        when(type.name()).thenReturn("SELL_LAND");
        when(specialOption.getType()).thenReturn(type);
        when(EventManager.getItem(MapLink.SPECIAL_OPTIONS, 0)).thenReturn(specialOption);
        ItemMap<Item> itemMap = mock(ItemMap.class);
        when(EventManager.getItemMap(MapLink.ACTION_LOGS)).thenReturn(itemMap);
        when(EventManager.getItem(MapLink.SPECIAL_OPTIONS, popup.getContentLinkID())).thenReturn(null);
        MultiPolygon mpolygon = PowerMockito.mock(MultiPolygon.class);
        when(popup.getMultiPolygon()).thenReturn(mpolygon);
        PowerMockito.when(popup.getType().name()).thenReturn("TYPENAME");
        PowerMockito.when(popup.getID()).thenReturn(0);
        List<Integer> visibleSHIDs = new LinkedList<Integer>();
        visibleSHIDs.add(0);
        visibleSHIDs.add(1);
        when(popup.getVisibleForStakeholderIDs()).thenReturn(visibleSHIDs);
        when(popup.getAnswers()).thenReturn(null);
        
        
    }
    
    @Test
    public void testPopup() throws TranslationException {
        when(popup.getContentMapLink()).thenReturn(MapLink.SPECIAL_OPTIONS);
        translator.translate(popup);
    }
}
