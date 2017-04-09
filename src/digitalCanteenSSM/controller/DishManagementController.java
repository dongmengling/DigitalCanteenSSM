package digitalCanteenSSM.controller;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.github.miemiedev.mybatis.paginator.domain.Order;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

import digitalCanteenSSM.exception.ResultInfo;
import digitalCanteenSSM.exception.SubmitResultInfo;
import digitalCanteenSSM.po.Detail;
import digitalCanteenSSM.po.Dish;
import digitalCanteenSSM.po.DishItems;
import digitalCanteenSSM.po.MUserItems;
import digitalCanteenSSM.po.Record;
import digitalCanteenSSM.service.DetailService;
import digitalCanteenSSM.service.DishManagementService;
import digitalCanteenSSM.service.DishPresetService;
import digitalCanteenSSM.service.DishTypePresetService;
import digitalCanteenSSM.service.RecordService;
import digitalCanteenSSM.service.UploadFileService;
import digitalCanteenSSM.service.WindowPresetService;

@Controller
public class DishManagementController {

	//图片实际存放路径，Tomcat虚拟路径定为/upload/pic，在页面dishManagement.jsp中使用到虚拟路径
	private static final  String picturePath = "E:\\webproject\\upload\\picture\\";
	
	//默认图片设置
	private static final  String defaultPicturePath = "default.jpg";
	private static final  String defaultUserPicturePath = "user-default.jpg";
	@Autowired
	private WindowPresetService windowPresetService;
	@Autowired
	private DishTypePresetService dishTypePresetService;
	@Autowired
	private DishPresetService dishPresetService;
	@Autowired
	private DishManagementService dishManagementService;
	@Autowired
	private UploadFileService uploadFileService;
	@Autowired
	private RecordService recordService;
	@Autowired
	private DetailService detailService;
	
	
	public static String getPicturepath() {
		return picturePath;
	}

	public static String getDefaultpicturepath() {
		return defaultPicturePath;
	}
	
	public static String getUserDefaultpicturepath() {
		return defaultUserPicturePath;
	}

	//菜品管理页面
	@RequestMapping ("/dishManagement")
	public ModelAndView dishManagement(HttpSession session) throws Exception{
		
		ModelAndView modelAndView = new ModelAndView();
		
		MUserItems muserItems = (MUserItems)session.getAttribute("muserItems");
		modelAndView.addObject("dishItemsList",dishManagementService.findDishInCanteen(muserItems.getCantID()));
		modelAndView.addObject("muserItems",muserItems);	
		if(session.getAttribute("ua").equals("pc")){
			modelAndView.setViewName("/WEB-INF/jsp/dishManagement.jsp");
		}else{
			modelAndView.setViewName("/WEB-INF/jsp/m_dishManagement.jsp");
		}
		
		
		return modelAndView;
	}
	
	//查找该管理员所属食堂下所有菜品
	@RequestMapping ("/findDishInCanteen")
	public ModelAndView findDishInCanteen(HttpSession session, HttpServletRequest request) throws Exception{
		//本段代码运行时优先从request中读取的页码和单页内容数量，
		//如果请求从其它controller发出，并无这两个对象，
		//则使用的是默认的值
		String pageNum = request.getParameter("pageNum");
		String pageSize = request.getParameter("pageSize");
		int num = 1;
		//菜品信息带有图片，所以一页只放五个元素
		int size = 5;
		if (pageNum != null && !"".equals(pageNum)) {
			num = Integer.parseInt(pageNum);
		}
		if (pageSize != null && !"".equals(pageSize)) {
			size = Integer.parseInt(pageSize);
		}
		
		//配置pagehelper的排序及分页
		String sortString = "id.desc";
		Order.formString(sortString);
		PageHelper.startPage(num, size);
		
		ModelAndView modelAndView = new ModelAndView();
		
		MUserItems muserItems = (MUserItems)session.getAttribute("muserItems");
		
		List<DishItems> dishItemsList = dishManagementService.findDishInCanteen(muserItems.getCantID());
		PageInfo<DishItems> pagehelper = new PageInfo<DishItems>(dishItemsList);
		
		modelAndView.addObject("pagehelper", pagehelper);
		modelAndView.addObject("muserItems",muserItems);	
		if(session.getAttribute("ua").equals("pc")){
			modelAndView.setViewName("/WEB-INF/jsp/dishInCanteen.jsp");
		}else{
			modelAndView.setViewName("/WEB-INF/jsp/m_dishInCanteen.jsp");
		}		
		
		return modelAndView;
	}
		
	//查找该管理员所属校区下所有菜品
	@RequestMapping ("/findDishInCampus")
	public ModelAndView findDishInCampus(HttpSession session) throws Exception{
		
		ModelAndView modelAndView = new ModelAndView();
		
		MUserItems muserItems = (MUserItems)session.getAttribute("muserItems");
		modelAndView.addObject("dishItemsList",dishManagementService.findDishInCampus(muserItems.getCampusID()));
		modelAndView.addObject("muserItems",muserItems);
		if(session.getAttribute("ua").equals("pc")){
			modelAndView.setViewName("/WEB-INF/jsp/dishInCampus.jsp");
		}else{
			modelAndView.setViewName("/WEB-INF/jsp/m_dishInCampus.jsp");
		}		
		
		return modelAndView;
	}

	//新申请的菜品审核页面
	@RequestMapping("/checkDish")
	public ModelAndView checkDish(HttpSession session) throws Exception{
		
		ModelAndView modelAndView = new ModelAndView();
		
		modelAndView.addObject("dishItemsList",dishManagementService.findApplyDish("待审核"));
		if(session.getAttribute("ua").equals("pc")){
			modelAndView.setViewName("/WEB-INF/jsp/dishCheck.jsp");
		}else{
			modelAndView.setViewName("/WEB-INF/jsp/m_dishCheck.jsp");
		}
		
		return modelAndView;
	}
	
	//审核通过
	@RequestMapping("/checkDishPass")
	public ModelAndView checkDishPass(Dish dish) throws Exception{
		
		ModelAndView modelAndView= new ModelAndView();
		
		dish.setDishInState("审核通过");
		dishManagementService.updateCheckDishPass(dish);
		modelAndView.setViewName("checkDish.action");
		
		return modelAndView;
	}
	
	//录入菜品的重复检测
	@RequestMapping("/importRepeatCheck")
	public @ResponseBody SubmitResultInfo importRepeatCheck(HttpSession session) throws Exception{
		
		ResultInfo resultInfo = new ResultInfo();
		
		//取得当前时间并格式化,用来记入记录表中
		SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd");  
		String dateString=simpleDateFormat.format(new Date());
		Date date=simpleDateFormat.parse(dateString);
		
		MUserItems muserItems = (MUserItems)session.getAttribute("muserItems");
		
		//新建一个record，填入操作人相关的字段信息以及时间，由于是当日录入，补录标志设置为0
		Record record = new Record(); 
		record.setRecordCampusID(muserItems.getCampusID());
		record.setRecordCantID(muserItems.getCantID());
		record.setRecordMUserID(muserItems.getMuserID());
		record.setRecordCampusName(muserItems.getCampusName());
		record.setRecordCantName(muserItems.getCantName());
		record.setRecordMUserName(muserItems.getMuserName());
		record.setRecordDate(date);
		record.setRecordSubmitState("已提交");
		record.setReplenishFlag(0);
		
		//到数据库中用食堂和日期信息查询今日是否已经生成过记录表，
		//如果没有生成，则生成一个新表；否则跳转到修改页面
		Record rec = recordService.findRecordInCanteenAndDate(record);
		if(rec == null){
			//将数据表存入数据库并获取表的id
			recordService.insertRecord(record);
			int recordid= recordService.findRecordID(record);
			
			resultInfo.setRecordID(recordid);
			resultInfo.setMessage("今日记录表创建成功");
			resultInfo.setType(ResultInfo.TYPE_RESULT_SUCCESS);				
		}else{
			//读取record的id
			int recordid= recordService.findRecordID(record);
			
			//将此消息的类型设定为info类型，方便前台页面判断要跳转的目标
			resultInfo.setRecordID(recordid);			
			resultInfo.setMessage("今日已生成过记录表");
			resultInfo.setType(ResultInfo.TYPE_RESULT_INFO);			
		}
		
		SubmitResultInfo submitResultInfo = new SubmitResultInfo(resultInfo);		
		return submitResultInfo;
	}
		
	//录入菜品页面显示
	@RequestMapping ("/importDish")
	public ModelAndView importDish(HttpSession session, HttpServletRequest request, Integer recordID) throws Exception{
		
		ModelAndView modelAndView = new ModelAndView();
		
		MUserItems muserItems = (MUserItems)session.getAttribute("muserItems");		
		Date recordDate = (Date) request.getAttribute("recordDate");
		
		modelAndView.addObject("recordID",recordID);	//传递记录表编号, 后面用于和detail表关联
		modelAndView.addObject("recordDate",recordDate);
		modelAndView.addObject("muserItems",muserItems);
		modelAndView.addObject("dishItemsList",dishManagementService.findDishInCanteen(muserItems.getCantID()));
		
		if(session.getAttribute("ua").equals("pc")){
			modelAndView.setViewName("/WEB-INF/jsp/dishImport.jsp");
		}else{
			modelAndView.setViewName("/WEB-INF/jsp/m_dishImport.jsp");
		}
		
		return modelAndView;
	}

	//补充录入以前日期的菜品
	@RequestMapping("/importReplenishDish")
	public ModelAndView importReplenishDish(HttpSession session,HttpServletRequest request) throws Exception{
		ModelAndView modelAndView = new ModelAndView();
		
		MUserItems muserItems = (MUserItems)session.getAttribute("muserItems");
		Date recordDate = (Date) request.getAttribute("recordDateReplenish");
		modelAndView.addObject("recordDate",recordDate);
		modelAndView.addObject("muserItems",muserItems);
		modelAndView.addObject("dishItemsList",dishManagementService.findDishInCanteen(muserItems.getCantID()));
		if(session.getAttribute("ua").equals("pc")){
			modelAndView.setViewName("/WEB-INF/jsp/dishImportReplenish.jsp");
		}else{
			modelAndView.setViewName("/WEB-INF/jsp/m_dishImportReplenish.jsp");
		}		
		
		return modelAndView;
	}
	
	//食堂管理员菜品录入处理
	@RequestMapping("/importHandle")
	public String importHandle(Integer[] dishIDList,HttpSession session,Date muserSubmitDate)throws Exception{
		
		Record record = new Record(); 
		DishItems dishItems = new DishItems();
		
		MUserItems muserItems = (MUserItems)session.getAttribute("muserItems");
		record.setRecordCampusID(muserItems.getCampusID());
		record.setRecordCantID(muserItems.getCantID());
		record.setRecordMUserID(muserItems.getMuserID());
		record.setRecordCampusName(muserItems.getCampusName());
		record.setRecordCantName(muserItems.getCantName());
		record.setRecordMUserName(muserItems.getMuserName());
		record.setRecordDate(muserSubmitDate);
		record.setRecordSubmitState("已提交");
		
		if(recordService.findRecordInCanteenAndDate(record) == null){
			
			recordService.insertRecord(record);
			int recordid= recordService.findRecordID(record);
			for(Integer i: dishIDList){
				dishItems = dishManagementService.findDishById(i);
				dishItems.setDishRecordID(recordid);
				detailService.insertDetailDish(dishItems);
			}
		}
		//不满足，即已录入当天的信息则返回提示信息
			
		return "forward:muserCanteenHostPage.action";
	}
	
	@RequestMapping("/getDishInImportDate")
	public ModelAndView getDishInImportDate(HttpSession session, HttpServletRequest request, Record record)throws Exception{
		ModelAndView modelAndView =new ModelAndView();
		
		if(!(recordService.findRecordByDate(record).isEmpty())){
			MUserItems muserItems = (MUserItems)session.getAttribute("muserItems");
			record.setRecordCantID(muserItems.getCantID());
			int recordid= recordService.findRecordID(record);
			request.setAttribute("recordDate",record.getRecordDate());
			List<Detail> dishDetailInDateList = detailService.findDetailDish(recordid);
			modelAndView.addObject("dishDetailInDateList",dishDetailInDateList);
			modelAndView.addObject("muserItems",muserItems);
			modelAndView.addObject("dishItemsList",dishManagementService.findDishInCanteen(muserItems.getCantID()));
		}
		modelAndView.setViewName("importDish.action");
		return modelAndView;
	}

	//补录
	@RequestMapping("/getDishInImportReplenishDate")
	public ModelAndView getDishInImportReplenishDate(HttpSession session,HttpServletRequest request,Record record,Date muserSubmitDate)throws Exception{
		ModelAndView modelAndView =new ModelAndView();
		if(!(recordService.findRecordByDate(record).isEmpty())){
			MUserItems muserItems = (MUserItems)session.getAttribute("muserItems");
			muserItems.setMuserSubmitDate(muserSubmitDate);
			record.setRecordCantID(muserItems.getCantID());
			int recordid= recordService.findRecordID(record);
			request.setAttribute("recordDateReplenish",record.getRecordDate());
			List<Detail> dishDetailInDateList = detailService.findDetailDish(recordid);
			modelAndView.addObject("dishDetailInDateList",dishDetailInDateList);
			modelAndView.addObject("muserItems",muserItems);
			modelAndView.addObject("dishItemsList",dishManagementService.findDishInCanteen(muserItems.getCantID()));
		}
		modelAndView.setViewName("importReplenishDish.action");
		return modelAndView;
	}
	
	/**
	 * 根据时间档查询菜品列表
	 * */
	@RequestMapping ("/chooseDishInDate")
	public ModelAndView chooseDishInDate(HttpSession session, String dishDate, Integer recordID,
			Integer replenishFlag, String replenishDate) throws Exception{
		
		ModelAndView modelAndView = new ModelAndView();
		
		MUserItems muserItems = (MUserItems)session.getAttribute("muserItems");
		
		int canteenID = muserItems.getCantID();
		DishItems dishItems = new DishItems();
		List<DishItems> dishItemsList = new ArrayList<DishItems>();
		
		//当选择范围是“全部”时，分别查询早餐和正餐的菜品列表，
		//然后用List的addAll方法把两个列表拼接成最终的列表；
		//当选择范围是早中晚餐时，只需要查询一次
		if(dishDate.equals("全部")){
			
			List<DishItems> dishItemsListTmp = new ArrayList<DishItems>();
			
			dishItems.setCantID(canteenID);
			dishItems.setDishDate("早餐");
			
			dishItemsListTmp = dishManagementService.findDishInCanteenAndDate(dishItems);
			
			dishItems.setDishDate("正餐");
			dishItemsList = dishManagementService.findDishInCanteenAndDate(dishItems);
			dishItemsList.addAll(dishItemsListTmp);
		}else{
			if(dishDate.equals("中餐")||dishDate.equals("晚餐")){
				
				dishItems.setDishDate("正餐");
				
			}else if(dishDate.equals("早餐")){
				
				dishItems.setDishDate("早餐");
				
			}
			dishItems.setCantID(canteenID);
			
			dishItemsList = dishManagementService.findDishInCanteenAndDate(dishItems);
		}
		
		//菜品查询时间档和记录表ID需要重新传回页面，页面加载时会根据这些信息来确定各下拉框的显示值
		modelAndView.addObject("dishDate", dishDate);	//时间档
		modelAndView.addObject("recordID", recordID);	//记录表编号
		modelAndView.addObject("muserItems", muserItems);
		modelAndView.addObject("dishItemsList", dishItemsList);
		
		//replenishFlag此处用于标记发出查询请求的页面是属于录入、补录还是修改页面
		if(replenishFlag == 0){
			modelAndView.setViewName("/WEB-INF/jsp/dishImport.jsp");
		}
		
		return modelAndView;
	}
	
	//添加已预置的菜品
	@RequestMapping ("/addDish")
	public ModelAndView addDish(HttpSession session) throws Exception{
		
		ModelAndView modelAndView = new ModelAndView();
		
		MUserItems muserItems = (MUserItems)session.getAttribute("muserItems");
		modelAndView.addObject("muserItems",muserItems);
		modelAndView.addObject("windowItemsList",windowPresetService.findWindowsInCanteen(muserItems.getCantID()));
		modelAndView.addObject("dishTypeList", dishTypePresetService.findAllDishType());
		modelAndView.addObject("dishPresetList",dishPresetService.findAllDishPreset());
		modelAndView.addObject("dishItemsList",dishManagementService.findAllDishes());
		if(session.getAttribute("ua").equals("pc")){
			modelAndView.setViewName("/WEB-INF/jsp/dishAdd.jsp");
		}else{
			modelAndView.setViewName("/WEB-INF/jsp/m_dishAdd.jsp");
		}
		
		return modelAndView;
	}
	
	//申请菜品页面显示
	@RequestMapping ("/applyDish")
	public ModelAndView applyDish(HttpSession session) throws Exception{
		
		ModelAndView modelAndView = new ModelAndView();
		
		MUserItems muserItems = (MUserItems)session.getAttribute("muserItems");
		modelAndView.addObject("muserItems",muserItems);
		modelAndView.addObject("windowItemsList",windowPresetService.findWindowsInCanteen(muserItems.getCantID()));
		modelAndView.addObject("dishTypeList", dishTypePresetService.findAllDishType());
		modelAndView.addObject("dishPresetList",dishPresetService.findAllDishPreset());
		modelAndView.addObject("dishItemsList",dishManagementService.findDishInCanteen(muserItems.getCantID()));
		
		if(session.getAttribute("ua").equals("pc")){
			modelAndView.setViewName("/WEB-INF/jsp/dishApply.jsp");
		}else{
			modelAndView.setViewName("/WEB-INF/jsp/m_dishApply.jsp");
		}
		
		return modelAndView;
	}	
	
	//通过菜品ID查找菜品信息
	@RequestMapping ("/findDishById")
	public DishItems findDishById(Integer dishID) throws Exception{
		
		return dishManagementService.findDishById(dishID);
	}
		
	//修改菜品：跳转到dishModify.jsp显示具体修改信息
	@RequestMapping ("/modifyDish")	
	public ModelAndView modifyDish(DishItems dishItems, HttpSession session) throws Exception{
		
		ModelAndView modelAndView = new ModelAndView();
			
		MUserItems muserItems = (MUserItems)session.getAttribute("muserItems");
		modelAndView.addObject("muserItems",muserItems);
		modelAndView.addObject("windowItemsList",windowPresetService.findWindowsInCanteen(dishItems.getWndCantID()));
		modelAndView.addObject("dishTypeList", dishTypePresetService.findAllDishType());
		modelAndView.addObject("dishPresetList",dishPresetService.findAllDishPreset());
		modelAndView.addObject("dishItems",findDishById(dishItems.getDishID()));	
		
		if(session.getAttribute("ua").equals("pc")){
			modelAndView.setViewName("/WEB-INF/jsp/dishModify.jsp");
		}else{
			modelAndView.setViewName("/WEB-INF/jsp/m_dishModify.jsp");
		}
		
		return modelAndView;
	}
	
	//修改菜品：修改之后保存并跳转到菜品录入页面
	@RequestMapping ("/modifyDishSave")	
	public ModelAndView modifyDishSave(DishItems dishItems,MultipartFile dishPhotoFile,HttpSession session) throws Exception{
		
		ModelAndView modelAndView = new ModelAndView();
		MUserItems muserItems = (MUserItems)session.getAttribute("muserItems");
		
		if(findDishByName(dishItems) == null || findDishByName(dishItems).getDishID() == dishItems.getDishID()){		
			String dishphoto=uploadFileService.uploadFile(dishPhotoFile, picturePath);
			//未改变图片则用原来的
			if( dishphoto== null){			
				DishItems dishitem = findDishById(dishItems.getDishID());
				dishItems.setDishPhoto(dishitem.getDishPhoto());
			}
			else{
				dishItems.setDishPhoto(dishphoto);
			}
			dishManagementService.updateDish(dishItems);
		}	
		modelAndView.addObject("muserItems",muserItems);	
		modelAndView.setViewName("findDishInCanteen.action");
		
		return modelAndView;
	}
	
	//通过菜品Name查找菜品信息供插入菜品信息时检查
	private DishItems findDishByName(DishItems dishItems) throws Exception {
		
		return dishManagementService.findDishByName(dishItems);
	}
	
	//插入新菜品信息
	@RequestMapping ("/insertDish")
	public ModelAndView insertDish(DishItems dishItems,MultipartFile dishPhotoFile,HttpSession session) throws Exception{
		
		ModelAndView modelAndView = new ModelAndView();

		if(findDishByName(dishItems) == null){	
			String dishphoto=uploadFileService.uploadFile(dishPhotoFile, picturePath);
			//未输入图片则使用默认的
			if( dishphoto== null){			
				dishItems.setDishPhoto(defaultPicturePath);
			}
			else{
				dishItems.setDishPhoto(dishphoto);
			}
			dishManagementService.insertDish(dishItems);
		}		
		modelAndView.setViewName("findDishInCanteen.action");
		
		return modelAndView;
	}
	
	//通过菜品ID删除菜品信息
	@RequestMapping ("/deleteDishById")
	public ModelAndView deleteDishById(Dish dish,HttpSession session) throws Exception{
		
		ModelAndView modelAndView = new ModelAndView();
		
		MUserItems muserItems = (MUserItems)session.getAttribute("muserItems");	
		dishManagementService.deleteDishById(dish);
		modelAndView.addObject("muserItems",muserItems);		
		modelAndView.setViewName("findDishInCanteen.action");
		
		return modelAndView;
	}
}
