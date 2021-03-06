package com.activity_order.controller;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.JSONObject;

import com.activity_order.model.*;
import com.activity_period.model.ActivityPeriodService;
import com.activity_period.model.ActivityPeriodVO;
import com.member.model.MemberVO;

public class ActivityOrderServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		doPost(req, res);
	}

	protected void doPost(HttpServletRequest req, HttpServletResponse res)throws ServletException, IOException {
		

			req.setCharacterEncoding("UTF-8");
			String action = req.getParameter("action");

			if ("getOne_For_Display".equals(action)) { // 來自select_page.jsp的請求

				List<String> errorMsgs = new LinkedList<String>();
				// Store this set in the request scope, in case we need to
				// send the ErrorPage view.
				req.setAttribute("errorMsgs", errorMsgs);

				try {
					/*************************** 1.接收請求參數 - 輸入格式的錯誤處理 **********************/
					String act_order_id = req.getParameter("act_order_id");
					if (act_order_id == null || (act_order_id.trim()).length() == 0) {
						errorMsgs.add("請輸入訂單編號");
					}
					// Send the use back to the form, if there were errors
					if (!errorMsgs.isEmpty()) {
						RequestDispatcher failureView = req.getRequestDispatcher("/back-end/souvenir_order/select_page.jsp");
						failureView.forward(req, res);
						return;// 程式中斷
					}

					
					try {
						if(!act_order_id.isEmpty()) act_order_id=act_order_id.trim();
					} catch (Exception e) {
						errorMsgs.add("訂單編號格式不正確");
					}
					// Send the use back to the form, if there were errors
					if (!errorMsgs.isEmpty()) {
						RequestDispatcher failureView = req.getRequestDispatcher("/back-end/souvenir_order/select_page.jsp");
						failureView.forward(req, res);
						return;// 程式中斷
					}

					/*************************** 2.開始查詢資料 *****************************************/
					ActivityOrderService actOrdSvc = new ActivityOrderService();
					ActivityOrderVO actordVO = actOrdSvc.getOneOrder(act_order_id);
					if (actordVO == null) {
						errorMsgs.add("查無資料");
					}
					// Send the use back to the form, if there were errors
					if (!errorMsgs.isEmpty()) {
						RequestDispatcher failureView = req.getRequestDispatcher("/back-end/souvenir_order/select_page.jsp");
						failureView.forward(req, res);
						return;// 程式中斷
					}

					/*************************** 3.查詢完成,準備轉交(Send the Success view) *************/
					req.setAttribute("actordVO", actordVO); // 資料庫取出的empVO物件,存入req
					String url = "/back-end/souvenir_order/listOneSouvenirOrder.jsp";
					RequestDispatcher successView = req.getRequestDispatcher(url); // 成功轉交 listOneSouvenirOrder.jsp
					successView.forward(req, res);

					/*************************** 其他可能的錯誤處理 *************************************/
				} catch (Exception e) {
					errorMsgs.add("無法取得資料:" + e.getMessage());
					RequestDispatcher failureView = req.getRequestDispatcher("/back-end/souvenir_order/select_page.jsp");
					failureView.forward(req, res);
				}
			}
			
			if ("memCancelActOrder".equals(action)) { // 來自listAllEmp.jsp的請求

				List<String> errorMsgs = new LinkedList<String>();
				// Store this set in the request scope, in case we need to
				// send the ErrorPage view.
				req.setAttribute("errorMsgs", errorMsgs);

				try {
					/*************************** 1.接收請求參數 ****************************************/
					String act_order_id = req.getParameter("act_order_id");

					/*************************** 2.開始查詢資料 ****************************************/
					ActivityOrderService actOrdSvc = new ActivityOrderService();
					ActivityOrderVO actordVO = actOrdSvc.getOneOrder(act_order_id);
					actordVO.setAct_order_status(2);//設定 訂單為 已取消狀態
					if(actordVO.getAct_payment_status()==2) {//若已付款才更改
						actordVO.setAct_payment_status(3);//設定付款狀態為 退款中
						
					}
					actOrdSvc.changeActOrderStatus(actordVO);
					/*************************** 3.查詢完成,準備轉交(Send the Success view) ************/
					String url = "/front-mem-end/activity_order/listOneActivityOrder.jsp";
					RequestDispatcher successView = req.getRequestDispatcher(url);// 成功轉交 update_emp_input.jsp
					successView.forward(req, res);

					/*************************** 其他可能的錯誤處理 **********************************/
				} catch (Exception e) {
					errorMsgs.add("無法取得要修改的資料:" + e.getMessage());
					RequestDispatcher failureView = req.getRequestDispatcher("/front-mem-end/activity_order/listOneActivityOrder.jsp");
					failureView.forward(req, res);
				}
			}
			
			
			
			if ("sellMemConfirmRefund".equals(action)) { // 來自listAllEmp.jsp的請求

				List<String> errorMsgs = new LinkedList<String>();
				// Store this set in the request scope, in case we need to
				// send the ErrorPage view.
				req.setAttribute("errorMsgs", errorMsgs);

				try {
					/*************************** 1.接收請求參數 ****************************************/
					String act_order_id = req.getParameter("act_order_id");

					/*************************** 2.開始查詢資料 ****************************************/
					ActivityOrderService actOrdSvc = new ActivityOrderService();
					ActivityOrderVO actordVO = actOrdSvc.getOneOrder(act_order_id);
					actordVO.setAct_order_status(2);//設定 訂單為 已取消狀態
					actordVO.setAct_payment_status(4);//設定付款狀態為 以退款
					actOrdSvc.changeActOrderStatus(actordVO);
					/*************************** 3.查詢完成,準備轉交(Send the Success view) ************/
					String url = "/front-sell-end/activity_order/sellListOneActivityOrder.jsp";
					RequestDispatcher successView = req.getRequestDispatcher(url);// 成功轉交 update_emp_input.jsp
					successView.forward(req, res);

					/*************************** 其他可能的錯誤處理 **********************************/
				} catch (Exception e) {
					errorMsgs.add("無法取得要修改的資料:" + e.getMessage());
					RequestDispatcher failureView = req.getRequestDispatcher("/front-sell-end/activity_order/sellListOneActivityOrder.jsp");
					failureView.forward(req, res);
				}
			}

//
//			if ("getOne_For_Update".equals(action)) { // 來自listAllEmp.jsp的請求
//
//				List<String> errorMsgs = new LinkedList<String>();
//				// Store this set in the request scope, in case we need to
//				// send the ErrorPage view.
//				req.setAttribute("errorMsgs", errorMsgs);
//
//				try {
//					/*************************** 1.接收請求參數 ****************************************/
//					String act_order_id = req.getParameter("act_order_id");
//
//					/*************************** 2.開始查詢資料 ****************************************/
//					ActivityOrderService actOrdSvc = new ActivityOrderService();
//					ActivityOrderVO actordVO = actOrdSvc.getOneOrder(act_order_id);
//
//					/*************************** 3.查詢完成,準備轉交(Send the Success view) ************/
//					req.setAttribute("actordVO", actordVO); // 資料庫取出的empVO物件,存入req
//					String url = "/back-end/souvenir_order/update_souvenirorder_input.jsp";
//					RequestDispatcher successView = req.getRequestDispatcher(url);// 成功轉交 update_emp_input.jsp
//					successView.forward(req, res);
//
//					/*************************** 其他可能的錯誤處理 **********************************/
//				} catch (Exception e) {
//					errorMsgs.add("無法取得要修改的資料:" + e.getMessage());
//					RequestDispatcher failureView = req.getRequestDispatcher("/back-end/souvenir_order/listAllSouvenirOrder.jsp");
//					failureView.forward(req, res);
//				}
//			}
//
//			if ("update".equals(action)) { // 來自update_souvenirorder_input.jsp的請求
//
//				List<String> errorMsgs = new LinkedList<String>();
//				// Store this set in the request scope, in case we need to
//				// send the ErrorPage view.
//				req.setAttribute("errorMsgs", errorMsgs);
//
//				try {
//					/*************************** 1.接收請求參數 - 輸入格式的錯誤處理 **********************/
//					String act_order_id = req.getParameter("act_order_id").trim();
//					String act_id = req.getParameter("act_id").trim();
////					String empIdReg = "^[(a-zA-Z0-9)]{2,100}$";
////					if (emp_id == null || emp_id.trim().length() == 0) {
////						errorMsgs.add("員工編號請勿空白");
////					} else if (!emp_id.trim().matches(empIdReg)) {
////						errorMsgs.add("員工編號: 只能是英文字母、數字 , 且長度必需在2到10之間");
////					}
//					String mem_id = req.getParameter(""
//							+ "").trim();
//					String memIdReg = "^[(a-zA-Z0-9)]{2,100}$";
//					if (mem_id == null || mem_id.trim().length() == 0) {
//						errorMsgs.add("會員編號請勿空白");
//					} else if (!mem_id.trim().matches(memIdReg)) {
//						errorMsgs.add("會員編號: 只能是英文字母、數字 , 且長度必需在2到10之間");
//					}
//					java.sql.Timestamp sou_order_date = java.sql.Timestamp.valueOf(req.getParameter("sou_order_date").trim());
//					String sou_receiver_name = req.getParameter("sou_receiver_name").trim();
////					if (sou_receiver_name == null || emp_id.trim().length() == 0) {
////						errorMsgs.add("收穫人姓名請勿空白");
////					} 
//					String sou_receiver_address = req.getParameter("sou_receiver_address").trim();		
////					if (sou_receiver_name == null || emp_id.trim().length() == 0) {
////						errorMsgs.add("收穫人地址請勿空白");
////					} 
//					
//					String sou_receiver_phone = req.getParameter("sou_receiver_phone").trim();
//					String sRPReg = "09\\d{8}";
//					if (sou_receiver_phone == null || sou_receiver_phone.trim().length() == 0) {
//						errorMsgs.add("收穫人電話請勿空白");
//					} else if (!sou_receiver_phone.trim().matches(sRPReg)) {
//						errorMsgs.add("收穫人電話: 只能是數字 ,開頭為09 且長度必需是10");
//					}
//					
//					Integer sou_shipment_fee = null	;
//					try {
//						sou_shipment_fee =new Integer(req.getParameter("sou_shipment_fee").trim());
//					} catch (NumberFormatException e) {
//						sou_shipment_fee = new Integer(0);
//						errorMsgs.add("請輸入數字.");
//					}
//					Integer sou_order_sum_price = null;
//					try {
//						sou_order_sum_price =new Integer(req.getParameter("sou_order_sum_price").trim());
//					} catch (NumberFormatException e) {
//						sou_order_sum_price = new Integer(0);
//						errorMsgs.add("請輸入數字.");
//					}
//					String sou_order_remarks = req.getParameter("sou_order_remarks");
//
//					Integer sou_shipping_method = null;
//					try {
//						sou_shipping_method =new Integer(req.getParameter("sou_shipping_method").trim());
//					} catch (NumberFormatException e) {
//						sou_shipping_method = new Integer(0);
//						errorMsgs.add("請輸入數字.");
//					}
//					Integer sou_order_status = null;
//					try {
//						sou_order_status =new Integer(req.getParameter("sou_order_status").trim());
//					} catch (NumberFormatException e) {
//						sou_order_status = new Integer(0);
//						errorMsgs.add("請輸入數字.");
//					}
//					Integer sou_payment_status = null;
//					try {
//						sou_payment_status =new Integer(req.getParameter("sou_payment_status").trim());
//					} catch (NumberFormatException e) {
//						sou_payment_status = new Integer(0);
//						errorMsgs.add("請輸入數字.");
//					}
//					Integer sou_shipment_status = null;
//					try {
//						sou_shipment_status =new Integer(req.getParameter("sou_shipment_status").trim());
//					} catch (NumberFormatException e) {
//						sou_shipment_status = new Integer(0);
//						errorMsgs.add("請輸入數字.");
//					}
//					
//					
//					
//
//
//					SouvenirOrderVO soVO = new SouvenirOrderVO();
//					soVO.setEmp_id(emp_id);
//					soVO.setMem_id(mem_id);
//					soVO.setSou_order_date(sou_order_date);
//					soVO.setSou_receiver_name(sou_receiver_name);
//					soVO.setSou_receiver_address(sou_receiver_address);
//					soVO.setSou_receiver_phone(sou_receiver_phone);
//					soVO.setSou_shipment_fee(sou_shipment_fee);
//					soVO.setSou_order_sum_price(sou_order_sum_price);
//					soVO.setSou_order_remarks(sou_order_remarks);
//					soVO.setSou_shipping_method(sou_shipping_method);
//					soVO.setSou_order_status(sou_order_status);
//					soVO.setSou_payment_status(sou_payment_status);
//					soVO.setSou_shipment_status(sou_shipment_status);
//					soVO.setSou_order_id(sou_order_id);
//					
//					// Send the use back to the form, if there were errors
//					if (!errorMsgs.isEmpty()) {
//						req.setAttribute("soVO", soVO); // 含有輸入格式錯誤的empVO物件,也存入req
//						RequestDispatcher failureView = req.getRequestDispatcher("/back-end/souvenir_order/update_souvenirorder_input.jsp");
//						failureView.forward(req, res);
//						return; // 程式中斷
//					}
//
//					/*************************** 2.開始修改資料 *****************************************/
//					SouvenirOrderService soSvc = new SouvenirOrderService();
//					soVO = soSvc.updateSouvenirOrder(emp_id, mem_id, sou_order_date,
//							sou_receiver_name, sou_receiver_address, sou_receiver_phone, sou_shipment_fee,
//							 sou_order_sum_price, sou_order_remarks, sou_shipping_method,
//							sou_order_status, sou_payment_status, sou_shipment_status, sou_order_id);
//
//					/*************************** 3.修改完成,準備轉交(Send the Success view) *************/
//					req.setAttribute("soVO", soVO); // 資料庫update成功後,正確的的empVO物件,存入req
//					String url = "/back-end/souvenir_order/listOneSouvenirOrder.jsp";
//					RequestDispatcher successView = req.getRequestDispatcher(url); // 修改成功後,轉交listOneEmp.jsp
//					successView.forward(req, res);
//
//					/*************************** 其他可能的錯誤處理 *************************************/
//				} catch (Exception e) {
//					errorMsgs.add("修改資料失敗:" + e.getMessage());
//					RequestDispatcher failureView = req.getRequestDispatcher("/back-end/souvenir_order/update_souvenirorder_input.jsp");
//					failureView.forward(req, res);
//				}
//			}
//
			if ("insert".equals(action)) { 

				List<String> errorMsgs = new LinkedList<String>();
				// Store this set in the request scope, in case we need to
				// send the ErrorPage view.
				req.setAttribute("errorMsgs", errorMsgs);

				try {
					/*********************** 1.接收請求參數 - 輸入格式的錯誤處理 *************************/
					//確認一般會員登入狀態
					HttpSession session = req.getSession();
					MemberVO memVO = (MemberVO) session.getAttribute("memVO");
					if(memVO==null) {   //確認是否登入 為登入導置登入頁面
						RequestDispatcher plsLogin = req.getRequestDispatcher("/front-mem-end/mem/memLogin.jsp");
						plsLogin.forward(req, res);
						return;
					}
					String mem_id = memVO.getMem_id();
					String act_period_id=req.getParameter("act_period_id");
					
					Integer act_order_amount=null;
					ActivityPeriodService actperSvc=new ActivityPeriodService();
					String act_order_amount_first=req.getParameter("act_order_amount");
					String actOrdAmountReg = "^[0-9]{1,}$";
					if(act_order_amount_first.matches(actOrdAmountReg)) {
					act_order_amount=Integer.parseInt(act_order_amount_first);
					}else {
						errorMsgs.add("下訂人數 請輸入正整數");
					}
					ActivityPeriodVO actperVO=actperSvc.getOneActPeriod(act_period_id);
					Double act_cur_price=actperVO.getAct_cur_price();
					Double act_sum_price=act_order_amount*act_cur_price;
					Integer act_order_status=1;//以確認訂單狀態
					Integer act_payment_status=0;//付款狀態為 未付款
					String act_order_remarks=req.getParameter("act_order_remarks");
					Integer act_sign_sum=actperVO.getAct_sign_sum();
					System.out.println(actperVO.getAct_up_limit() +"\n"+actperVO.getAct_sign_sum()+"\n"+act_order_amount);
					if((actperVO.getAct_up_limit()<(actperVO.getAct_sign_sum()+act_order_amount))) {
						errorMsgs.add("報名名額超過活動上限");
					}

					ActivityOrderVO actordVO = new ActivityOrderVO();
					actordVO.setMem_id(mem_id);
					actordVO.setAct_period_id(act_period_id);
					actordVO.setAct_order_amount(act_order_amount);
					actordVO.setAct_sum_price(act_sum_price);
					actordVO.setAct_order_status(act_order_status);
					actordVO.setAct_payment_status(act_payment_status);
					actordVO.setAct_order_remarks(act_order_remarks);
					
					
					if(!errorMsgs.isEmpty()) {
						RequestDispatcher failureView = req.getRequestDispatcher("/front-mem-end/activity_order/listOneActivityOrder.jsp");
						failureView.forward(req, res);
						return;
					}

					/*************************** 2.開始新增資料 ***************************************/
					ActivityOrderService actordSvc = new ActivityOrderService();
					actordSvc.insertActivityOrder(actordVO);
					actperSvc.upDateActPerSignSum(act_period_id,act_sign_sum+act_order_amount);
					String act_period_start_formate=actperSvc.getOneActPeriod(act_period_id).getAct_period_start()
							.toString().substring(0, 11).trim();
					System.out.println(act_period_start_formate);
					/*************************** 3.新增完成,準備轉交(Send the Success view) ***********/
					
					HttpSession userSession = req.getSession();
					Set<javax.websocket.Session> wsSessions = (Set<javax.websocket.Session>) userSession.getAttribute("wsSessions");
					if (wsSessions != null && wsSessions.size() > 0) {
						JSONObject data = new JSONObject();
						data.put("type", "活動訂單");
						data.put("msg", "您有一筆新的活動訂單成立囉~!");
						data.put("checkInDate", act_period_start_formate);
						wsSessions.forEach(e -> e.getAsyncRemote().sendText(data.toString()));
					}
					
					
					
					String url =req.getContextPath() + "/front-mem-end/activity_order/listOneActivityOrder.jsp";
					res.sendRedirect(url);
					

					/*************************** 其他可能的錯誤處理 **********************************/
				} catch (Exception e) {
					errorMsgs.add(e.getMessage());
					RequestDispatcher failureView = req.getRequestDispatcher("/front-mem-end/activity_order/listOneActivityOrder.jsp");
					failureView.forward(req, res);
				}
			}
//			會員付款=============================================================================
			
			if ("memPayTheBill".equals(action)) { 

				List<String> errorMsgs = new LinkedList<String>();
				// Store this set in the request scope, in case we need to
				// send the ErrorPage view.
				req.setAttribute("errorMsgs", errorMsgs);

				try {
					/*********************** 1.接收請求參數 - 輸入格式的錯誤處理 *************************/
					//確認一般會員登入狀態
					HttpSession session = req.getSession();
					MemberVO memVO = (MemberVO) session.getAttribute("memVO");
					
					String mem_id = memVO.getMem_id();
					String act_order_id=req.getParameter("act_order_id");
					ActivityOrderService actordSvc=new ActivityOrderService();
					ActivityOrderVO actordVO=actordSvc.getOneOrder(act_order_id);

					actordVO.setAct_payment_status(2);//更改狀態為已付款
			

					/*************************** 2.開始新增資料 ***************************************/
					
					actordSvc.changeActOrderStatus(actordVO);

					/*************************** 3.新增完成,準備轉交(Send the Success view) ***********/
					String url = "/front-mem-end/activity_order/listOneActivityOrder.jsp";
					RequestDispatcher successView = req.getRequestDispatcher(url); // 新增成功後轉交listAllEmp.jsp
					successView.forward(req, res);

					/*************************** 其他可能的錯誤處理 **********************************/
				} catch (Exception e) {
					errorMsgs.add(e.getMessage());
					RequestDispatcher failureView = req.getRequestDispatcher("/back-end/souvenir_order/addSouvenirOrder.jsp");
					failureView.forward(req, res);
				}
			}
			
			
			
			
//
//			if ("delete".equals(action)) { // 來自listAllSouvenir_Order.jsp
//
//				List<String> errorMsgs = new LinkedList<String>();
//				// Store this set in the request scope, in case we need to
//				// send the ErrorPage view.
//				req.setAttribute("errorMsgs", errorMsgs);
//
//				try {
//					/*************************** 1.接收請求參數 ***************************************/
//					String sou_order_id = req.getParameter("sou_order_id");
//
//					/*************************** 2.開始刪除資料 ***************************************/
//					SouvenirOrderService soSvc = new SouvenirOrderService();
//					soSvc.deleteSouvenirOrder(sou_order_id);
//
//					/*************************** 3.刪除完成,準備轉交(Send the Success view) ***********/
//					String url = "/back-end/souvenir_order/listAllSouvenirOrder.jsp";
//					RequestDispatcher successView = req.getRequestDispatcher(url);// 刪除成功後,轉交回送出刪除的來源網頁
//					successView.forward(req, res);
//
//					/*************************** 其他可能的錯誤處理 **********************************/
//				} catch (Exception e) {
//					errorMsgs.add("刪除資料失敗:" + e.getMessage());
//					RequestDispatcher failureView;
//					failureView = req.getRequestDispatcher("/back-end/souvenir_order/listAllSouvenirOrder.jsp");
//					failureView.forward(req, res);
//				}
//			}
//			if ("listSouvenirOrders_ByCompositeQuery".equals(action)) { // 來自select_page.jsp的複合查詢請求
//				List<String> errorMsgs = new LinkedList<String>();
//				// Store this set in the request scope, in case we need to
//				// send the ErrorPage view.
//				req.setAttribute("errorMsgs", errorMsgs);
//
//				try {
//					
//					/***************************1.將輸入資料轉為Map**********************************/ 
//					//採用Map<String,String[]> getParameterMap()的方法 
//					//注意:an immutable java.util.Map 
//					Map<String, String[]> map = req.getParameterMap();
//					
//					/***************************2.開始複合查詢***************************************/
//					SouvenirOrderService soSvc = new SouvenirOrderService();
//					List<SouvenirOrderVO> list  = soSvc.getAll(map);
//					
//					/***************************3.查詢完成,準備轉交(Send the Success view)************/
//					req.setAttribute("listSouvenirOrders_ByCompositeQuery", list); // 資料庫取出的list物件,存入request
//					RequestDispatcher successView = req.getRequestDispatcher("/back-end/souvenir_order/listSouvenirOrders_ByCompositeQuery.jsp"); // 成功轉交listEmps_ByCompositeQuery.jsp
//					successView.forward(req, res);
//					
//					/***************************其他可能的錯誤處理**********************************/
//				} catch (Exception e) {
//					errorMsgs.add(e.getMessage());
//					RequestDispatcher failureView = req
//							.getRequestDispatcher("/back-end/souvenir_order/select_page.jsp");
//					failureView.forward(req, res);
//				}
//			}	
		}
	}
	



