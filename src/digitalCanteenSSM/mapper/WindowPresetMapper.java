package digitalCanteenSSM.mapper;

import java.util.List;

import digitalCanteenSSM.po.Window;
import digitalCanteenSSM.po.WindowItems;

public interface WindowPresetMapper {

	public List<WindowItems> findAllWindows() throws Exception;
	
	public WindowItems findWindowById(Integer wndID) throws Exception;
	
	public WindowItems findWindowByName(WindowItems windowItems) throws Exception;
	
	public List<Window> findWindowsInCanteen(Integer cantID) throws Exception;
	
	public void updateWindow(WindowItems windowItems) throws Exception;
	
	public void deleteWindowById(Window window) throws Exception;
	
	public void insertWindow(Window window) throws Exception;
}
