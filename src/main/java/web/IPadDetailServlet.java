
package web;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import dao.DetailDao;
import dto.Detail_dto;

@WebServlet("/IPadDetailServlet")
public class IPadDetailServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private static final String JSP_PATH = "/WEB-INF/jsp/iPad_detail.jsp";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String idStr = req.getParameter("id");
        String tab = req.getParameter("tab");

        if (tab == null || tab.isEmpty()) {
            tab = "terminal";
        }

        if (idStr == null || idStr.isEmpty()) {
            resp.sendRedirect(req.getContextPath() + "/TerminalServlet");
            return;
        }

        int id = Integer.parseInt(idStr);

        DetailDao dao = new DetailDao();
        Detail_dto detail = dao.findTerminalById(id);

        if (detail == null) {
            resp.sendRedirect(req.getContextPath() + "/TerminalServlet");
            return;
        }
        HttpSession session = req.getSession(false);

        Integer roleId = getRoleId(session);
        boolean isRole89 = roleId != null && roleId.intValue() == 89;
        boolean isRole99 = roleId != null && roleId.intValue() == 99;
        boolean isIbsTerminal = dao.isIbsTerminal( detail.getSerialNumber());
        boolean isIbsInventoryOwner = dao.existsIbsInventoryOwner( detail.getSerialNumber());
        boolean companyFixed = isRole89 && isIbsTerminal;
        boolean ownerAddReadOnly = isRole99 && isIbsInventoryOwner;
        req.setAttribute( "roleId", roleId);
        req.setAttribute( "isIbsTerminal", isIbsTerminal);
        req.setAttribute( "companyFixed", companyFixed);
        req.setAttribute( "ownerAddReadOnly", ownerAddReadOnly);
        System.out.println(
                "[IPadDetailServlet] roleId="
                + roleId
                + ", isIbsTerminal="
                + isIbsTerminal
                + ", isIbsInventoryOwner="
                + isIbsInventoryOwner
                + ", companyFixed="
                + companyFixed
                + ", ownerAddReadOnly="
                + ownerAddReadOnly
        );

        detail.setCompanyOptions(dao.getCompanyList());
        detail.setDepartmentOptions(dao.getDepartmentList());
        detail.setEmployeeOptions(dao.getLatestEmployeeList());

        List<Detail_dto> ownerHistory = dao.findOwnerHistoryBySerialNumber(detail.getSerialNumber());

        req.setAttribute("detail", detail);
        req.setAttribute("ownerHistory", ownerHistory);
        req.setAttribute("tab", tab);

        req.getRequestDispatcher(JSP_PATH).forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        req.setCharacterEncoding(StandardCharsets.UTF_8.name());

        String action = req.getParameter("action");
        String tab = req.getParameter("tab");
        String idStr = req.getParameter("id");
    	
    	System.out.println(
    		    "[IPadDetailServlet] action="
    		    + action
    		    + ", tab="
    		    + tab
    		);


        if (tab == null || tab.isEmpty()) { tab = "terminal";}

        if (idStr == null || idStr.isEmpty()) { resp.sendRedirect(req.getContextPath() + "/TerminalServlet");
            return;
        }

        int id = Integer.parseInt(idStr);

        HttpSession session = req.getSession(false);
        String loginUserId = null;

        if (session != null) {
            Object userIdObj = session.getAttribute("authUserId");
            if (userIdObj != null) {
                loginUserId = userIdObj.toString();
            }
        }

        if (loginUserId == null || loginUserId.isEmpty()) {
            req.setAttribute("errors", java.util.Arrays.asList("ログインユーザー情報が取得できませんでした。"));
            doGet(req, resp);
            return;
        }

        DetailDao dao = new DetailDao();
        List<String> errors = new ArrayList<>();

        try {
            if ("updateTerminal".equals(action)) {
                Detail_dto dto = new Detail_dto();

                dto.setId(id);

                // 画面上 readonly の項目は更新対象外
                // assetNumber / serialNumber / udid / macAddress はセットしない

                dto.setInnoHin(trim(req.getParameter("innoHin")));
                dto.setTel(trim(req.getParameter("tel")));
                dto.setContractDate(parseDate(req.getParameter("contractDate")));
                dto.setContractPeriod(parseDate(req.getParameter("contractPeriod")));
                dto.setTerminationDate(parseDate(req.getParameter("terminationDate")));
                dto.setRentalCompany(trim(req.getParameter("rentalCompany")));

                String tankaStr = trim(req.getParameter("tanka"));
                if (tankaStr != null && !tankaStr.isEmpty()) {
                    dto.setTanka(Integer.parseInt(tankaStr.replace(",", "")));
                } else {
                    dto.setTanka(null);
                }

                String serialNumber = trim(req.getParameter("serialNumber"));

                System.out.println(
                    "[SQL][DetailDao.updateTerminal] "
                    + "id=" + id
                    + ", serialNumber=" + serialNumber
                    + ", loginUserId=" + loginUserId
                );

                System.out.println(
                    "[SQL][DetailDao.updateTerminal] params: "
                    + "innoHin=" + dto.getInnoHin()
                    + ", tel=" + dto.getTel()
                    + ", contractDate=" + dto.getContractDate()
                    + ", contractPeriod=" + dto.getContractPeriod()
                    + ", terminationDate=" + dto.getTerminationDate()
                    + ", rentalCompany=" + dto.getRentalCompany()
                    + ", tanka=" + dto.getTanka()
                );

                long start = System.currentTimeMillis();
                int updateCount = dao.updateTerminal(dto, loginUserId);
                long end = System.currentTimeMillis();

                System.out.println(
                    "[SQL][DetailDao.updateTerminal] updateCount="
                    + updateCount
                    + " (update took "
                    + (end - start)
                    + " ms)"
                );

                if (updateCount > 0) {
                    req.setAttribute("message", "端末情報を更新しました。");
                } else {
                    errors.add("更新対象の端末情報が見つかりませんでした。");
                }

                tab = "terminal";

            } else if ("deleteTerminal".equals(action)) {

                String serialNumber = trim(req.getParameter("serialNumber"));

                System.out.println(
                    "[SQL][DetailDao.deleteTerminalBySerialNumber] "
                    + "serialNumber=" + serialNumber
                    + ", loginUserId=" + loginUserId
                );

                if (serialNumber == null || serialNumber.isEmpty()) {

                    errors.add("シリアル番号が取得できませんでした。");

                    System.out.println(
                        "[SQL][DetailDao.deleteTerminalBySerialNumber] serialNumber is empty"
                    );

                    tab = "terminal";

                } else {

                    long start = System.currentTimeMillis();
                    int count = dao.deleteTerminalBySerialNumber(serialNumber, loginUserId);
                    long end = System.currentTimeMillis();

                    System.out.println(
                        "[SQL][DetailDao.deleteTerminalBySerialNumber] updateCount="
                        + count
                        + " (update took "
                        + (end - start)
                        + " ms)"
                    );

                    if (count > 0) {

                        System.out.println(
                            "[AccessLog] deleteTerminal success. serialNumber="
                            + serialNumber
                        );

                        resp.sendRedirect(req.getContextPath() + "/TerminalServlet");
                        return;

                    } else {

                        errors.add("削除対象の端末情報が見つかりませんでした。");

                        System.out.println(
                            "[AccessLog] deleteTerminal target not found. serialNumber="
                            + serialNumber
                        );

                        tab = "terminal";
                    }
                }

            } else if ("addOwner".equals(action)) {
            	
            	Integer roleId = getRoleId(session);

            	boolean isRole89 = roleId != null && roleId.intValue() == 89;
            	boolean isRole99 = roleId != null && roleId.intValue() == 99;
                long addOwnerStart = System.currentTimeMillis();

                System.out.println(
                    "[SQL][IPadDetailServlet.addOwner] start "
                    + "id=" + id
                    + ", loginUserId=" + loginUserId
                    + ", companyId=" + trim(req.getParameter("companyId"))
                    + ", departmentId=" + trim(req.getParameter("departmentId"))
                    + ", employeeId=" + trim(req.getParameter("employeeId"))
                    + ", distributionDate=" + trim(req.getParameter("distributionDate"))
                    + ", longRangeUser=" + trim(req.getParameter("longRangeUser"))
                    + ", jisysInventory=" + trim(req.getParameter("jisysInventory"))
                    + ", jisysInventoryDate=" + trim(req.getParameter("jisysInventoryDate"))
                );


                Detail_dto terminal = dao.findTerminalById(id);                
                boolean isIbsTerminal = terminal != null && dao.isIbsTerminal( terminal.getSerialNumber());                
                boolean isIbsInventoryOwner = terminal != null && dao.existsIbsInventoryOwner( terminal.getSerialNumber());

                if (isRole99 && isIbsInventoryOwner) {
                    errors.add( "IBS管理端末のため、この権限では利用者情報を追加できません。");
                    tab = "ownerAdd";

                } else if (isRole89 && !isIbsTerminal) {
                    errors.add( "IBS端末以外には利用者情報を追加できません。");
                    tab = "ownerAdd";
                }

                if (!errors.isEmpty()) { req.setAttribute("errors", errors);}
                else if (terminal == null) { errors.add("端末情報が取得できませんでした。"); tab = "ownerAdd";
                    
                System.out.println(
                "[SQL][IPadDetailServlet.addOwner] end "
                + "(process took "
                + (System.currentTimeMillis() - addOwnerStart)
                + " ms)"
                );

                } else {
                    String jisysInventoryDate = trim(req.getParameter("jisysInventoryDate"));

                    if (jisysInventoryDate != null && !jisysInventoryDate.isEmpty()) {
                        String serialNumber = terminal.getSerialNumber();

                        if (serialNumber == null || serialNumber.isEmpty()) {
                            errors.add("シリアル番号が取得できませんでした。");
                        }

                        Date jisysInventoryDistributionDate = parseDate(jisysInventoryDate);

                        if (jisysInventoryDistributionDate == null) {
                            errors.add("情シス在庫にチェックがありますが日付が指定されていません");
                        }

                        if (errors.isEmpty()) {
                            System.out.println(
                                "[SQL][DetailDao.registerJisysInventoryOwner] "
                                + "serialNumber=" + serialNumber
                                + ", distributionDate=" + jisysInventoryDistributionDate
                                + ", loginUserId=" + loginUserId
                            );

                            long start = System.currentTimeMillis();

                            int count = dao.registerJisysInventoryOwner(
                                serialNumber,
                                jisysInventoryDistributionDate,
                                loginUserId
                            );

                            long end = System.currentTimeMillis();

                            System.out.println(
                                "[SQL][DetailDao.registerJisysInventoryOwner] resultCount="
                                + count
                                + " (process took "
                                + (end - start)
                                + " ms)"
                            );

                            if (count > 0) {
                                req.setAttribute("message", "情シス在庫として登録しました。");
                            } else {
                                errors.add("情シス在庫への更新対象が見つかりませんでした。");
                            }
                        }

                        tab = "ownerAdd";

                    } else {
                        Detail_dto dto = new Detail_dto();

                        dto.setAssetNumber(terminal.getAssetNumber());
                        dto.setSerialNumber(terminal.getSerialNumber());
                        String requestedCompanyId = trim(req.getParameter("companyId"));
                        if (isRole89 && isIbsTerminal) {
                            dto.setCompanyId("7000");
                        } else {
                            dto.setCompanyId( requestedCompanyId);
                        }
                        dto.setDepartmentId(trim(req.getParameter("departmentId")));
                        dto.setEmployeeId(trim(req.getParameter("employeeId")));
                        dto.setDistributionDate(parseDate(req.getParameter("distributionDate")));
                        dto.setLongRangeUser(trim(req.getParameter("longRangeUser")));

                        // 返却日は画面から削除したため常に null
                        dto.setReturnDate(null);

                        if (dto.getDistributionDate() == null) {
                            errors.add("配付日は必須です。");
                        }

                        if (dto.getEmployeeId() == null || dto.getEmployeeId().isEmpty()) {
                            errors.add("使用者は必須です。");
                        }

                        String[] emp = null;

                        if (errors.isEmpty()) {
                            emp = dao.findLatestEmployeeById(dto.getEmployeeId());

                            if (emp == null) {
                                errors.add("使用者情報が取得できませんでした。");
                            } else {
                                dto.setOwnerNum(emp[2]);
                                dto.setOwnerName(emp[3]);
                            }
                        }

                        if (!errors.isEmpty()) {
                            req.setAttribute("errors", errors);
                        } else {
                            int insertCount = dao.insertOwner(dto, loginUserId);

                            int inventoryOffCount = dao.updateTerminalInventoryOff(
                                terminal.getSerialNumber(),
                                loginUserId
                            );

                            System.out.println(
                                "[SQL][IPadDetailServlet.addOwner] result: "
                                + "insertOwnerCount=" + insertCount
                                + ", inventoryOffCount=" + inventoryOffCount
                                + ", serialNumber=" + terminal.getSerialNumber()
                            );

                            req.setAttribute("message", "利用者情報を追加しました。");
                        }

                        tab = "ownerAdd";
                    }
                }

            } else if ("registerFault".equals(action)) {

                System.out.println(
                    "[IPadDetailServlet] registerFault START"
                );

                Detail_dto terminal =
                        dao.findTerminalById(id);

                if (terminal == null) {

                    errors.add(
                        "端末情報が取得できませんでした。"
                    );

                } else {

                    String serialNumber =
                            terminal.getSerialNumber();

                    /*
                     * 1. 画面上の返却日入力値を取得
                     *
                     * returnTarget:
                     * ownerNum|distributionDate
                     *
                     * returnDate:
                     * 画面で入力された返却日
                     *
                     * originalReturnDate:
                     * DB取得時点の返却日
                     */
                    String[] returnTargets =
                            req.getParameterValues(
                                "returnTarget"
                            );

                    String[] returnDates =
                            req.getParameterValues(
                                "returnDate"
                            );

                    String[] originalReturnDates =
                            req.getParameterValues(
                                "originalReturnDate"
                            );

                    System.out.println(
                        "[IPadDetailServlet.registerFault] "
                        + "returnTargets="
                        + (returnTargets == null
                            ? "null"
                            : returnTargets.length)
                        + ", returnDates="
                        + (returnDates == null
                            ? "null"
                            : returnDates.length)
                        + ", originalReturnDates="
                        + (originalReturnDates == null
                            ? "null"
                            : originalReturnDates.length)
                    );

                    /*
                     * 故障登録時に、同じ行へ返却日も入力されているか
                     * 判定するためのMap
                     *
                     * key:
                     * ownerNum|distributionDate
                     *
                     * value:
                     * 入力された返却日
                     */
                    Map<String, Date> returnDateMap =
                            new HashMap<>();

                    if (returnTargets != null
                            && returnDates != null) {

                        int len = Math.min(
                            returnTargets.length,
                            returnDates.length
                        );

                        for (int i = 0; i < len; i++) {

                            String target =
                                    trim(returnTargets[i]);

                            String returnDateStr =
                                    trim(returnDates[i]);

                            if (target == null
                                    || target.isEmpty()) {

                                continue;
                            }

                            if (returnDateStr != null
                                    && !returnDateStr.isEmpty()) {

                                Date inputReturnDate =
                                        parseDate(
                                            returnDateStr
                                        );

                                returnDateMap.put(
                                    target,
                                    inputReturnDate
                                );
                            }
                        }
                    }

                    /*
                     * 2. 故障チェック登録
                     *
                     * 返却日だけを登録するときは
                     * faultTargetsがnullになる。
                     *
                     * この処理と返却日更新処理は
                     * 別々に実行する。
                     */
                    String[] faultTargets =
                            req.getParameterValues(
                                "faultTarget"
                            );

                    int faultInsertCount = 0;
                    int faultReturnUpdateCount = 0;

                    if (faultTargets != null
                            && faultTargets.length > 0) {

                        for (String target
                                : faultTargets) {

                            if (target == null
                                    || target.isEmpty()) {

                                continue;
                            }

                            String[] parts =
                                    target.split(
                                        "\\|",
                                        -1
                                    );

                            if (parts.length < 2) {

                                continue;
                            }

                            String employeeId =
                                    trim(parts[0]);

                            Date distributionDate =
                                    parseDate(parts[1]);

                            if (employeeId == null
                                    || employeeId.isEmpty()
                                    || distributionDate == null) {

                                continue;
                            }

                            Date inputReturnDate =
                                    returnDateMap.get(
                                        target
                                    );

                            boolean hasReturnDateOnScreen =
                                    inputReturnDate != null;

                            /*
                             * 返却日が同時入力されていない場合だけ、
                             * 既に返却済みかを確認する。
                             *
                             * 返却日が同時入力されている場合は、
                             * この後の返却日更新処理で返却済みにするため、
                             * ここではスキップ判定しない。
                             */
                            if (!hasReturnDateOnScreen) {

                                boolean returned =
                                        dao.isReturnedOwner(
                                            serialNumber,
                                            employeeId,
                                            distributionDate
                                        );

                                if (returned) {

                                    System.out.println(
                                        "[IPadDetailServlet.registerFault] "
                                        + "skip returned owner: "
                                        + "serialNumber="
                                        + serialNumber
                                        + ", employeeId="
                                        + employeeId
                                        + ", distributionDate="
                                        + distributionDate
                                    );

                                    continue;
                                }
                            }

                            /*
                             * 同じ故障情報が既に登録済みの場合は
                             * 二重登録しない。
                             */
                            if (dao.existsFault(
                                    employeeId,
                                    serialNumber,
                                    distributionDate)) {

                                System.out.println(
                                    "[IPadDetailServlet.registerFault] "
                                    + "skip existing fault: "
                                    + "serialNumber="
                                    + serialNumber
                                    + ", employeeId="
                                    + employeeId
                                    + ", distributionDate="
                                    + distributionDate
                                );

                                continue;
                            }

                            System.out.println(
                                "[SQL][DetailDao.insertFaultManagement] "
                                + "serialNumber="
                                + serialNumber
                                + ", employeeId="
                                + employeeId
                                + ", distributionDate="
                                + distributionDate
                                + ", hasReturnDateOnScreen="
                                + hasReturnDateOnScreen
                                + ", loginUserId="
                                + loginUserId
                            );

                            int inserted =
                                    dao.insertFaultManagement(
                                        terminal,
                                        employeeId,
                                        distributionDate,
                                        loginUserId
                                    );

                            if (inserted > 0) {

                                faultInsertCount++;

                                /*
                                 * 故障情報を登録できた場合は、
                                 * terminal_managementの
                                 * is_fault_returnを1へ更新する。
                                 */
                                int faultReturnUpdated =
                                        dao.updateTerminalFaultReturn(
                                            serialNumber,
                                            loginUserId
                                        );

                                if (faultReturnUpdated > 0) {

                                    faultReturnUpdateCount++;
                                }

                                System.out.println(
                                    "[SQL][IPadDetailServlet.registerFault] "
                                    + "updateTerminalFaultReturn result: "
                                    + "count="
                                    + faultReturnUpdated
                                    + ", serialNumber="
                                    + serialNumber
                                );
                            }
                        }
                    }

                    /*
                     * 3. 返却日更新
                     *
                     * 故障チェックの有無とは関係なく実行する。
                     * 今回の修正で重要な部分。
                     */
                    int returnUpdateCount = 0;

                    if (returnTargets != null
                            && returnDates != null) {

                        int len = Math.min(
                            returnTargets.length,
                            returnDates.length
                        );

                        for (int i = 0; i < len; i++) {

                            String target =
                                    trim(returnTargets[i]);

                            String returnDateStr =
                                    trim(returnDates[i]);

                            String originalReturnDateStr =
                                    "";

                            if (originalReturnDates != null
                                    && i
                                    < originalReturnDates.length) {

                                originalReturnDateStr =
                                        trim(
                                            originalReturnDates[i]
                                        );
                            }

                            System.out.println(
                                "[IPadDetailServlet.registerFault] "
                                + "return row="
                                + i
                                + ", target="
                                + target
                                + ", returnDate="
                                + returnDateStr
                                + ", originalReturnDate="
                                + originalReturnDateStr
                            );

                            /*
                             * 返却日が空の場合は更新しない。
                             */
                            if (returnDateStr == null
                                    || returnDateStr.isEmpty()) {

                                continue;
                            }

                            /*
                             * 画面表示時から変更されていない場合は
                             * 更新しない。
                             */
                            if (returnDateStr.equals(
                                    originalReturnDateStr)) {

                                continue;
                            }

                            if (target == null
                                    || target.isEmpty()) {

                                errors.add(
                                    "返却日の更新対象が取得できませんでした。"
                                );

                                continue;
                            }

                            String[] parts =
                                    target.split(
                                        "\\|",
                                        -1
                                    );

                            if (parts.length < 2) {

                                errors.add(
                                    "返却日の更新対象が不正です。"
                                );

                                continue;
                            }

                            String ownerNum =
                                    trim(parts[0]);

                            Date distributionDate =
                                    parseDate(parts[1]);

                            Date returnDate =
                                    parseDate(
                                        returnDateStr
                                    );

                            if (ownerNum == null
                                    || ownerNum.isEmpty()) {

                                errors.add(
                                    "返却日の更新対象ユーザーが取得できませんでした。"
                                );

                                continue;
                            }

                            if (distributionDate == null) {

                                errors.add(
                                    "返却日の更新対象の配付日が取得できませんでした。"
                                );

                                continue;
                            }

                            if (returnDate == null) {

                                errors.add(
                                    "返却日の日付形式が不正です。"
                                );

                                continue;
                            }

                            System.out.println(
                                "[SQL][DetailDao.updateOwnerReturnDate] "
                                + "serialNumber="
                                + serialNumber
                                + ", ownerNum="
                                + ownerNum
                                + ", distributionDate="
                                + distributionDate
                                + ", returnDate="
                                + returnDate
                                + ", loginUserId="
                                + loginUserId
                            );

                            int count =
                                    dao.updateOwnerReturnDate(
                                        serialNumber,
                                        ownerNum,
                                        distributionDate,
                                        returnDate,
                                        loginUserId
                                    );

                            if (count > 0) {

                                returnUpdateCount++;

                            } else {

                                errors.add(
                                    "返却日の更新対象が見つかりませんでした。"
                                );
                            }
                        }
                    }

                    /*
                     * 4. 完了メッセージ作成
                     */
                    if (errors.isEmpty()) {

                        List<String> messages =
                                new ArrayList<>();

                        if (returnUpdateCount > 0) {

                            messages.add(
                                returnUpdateCount
                                + "件の返却日を更新しました。"
                            );
                        }

                        if (faultInsertCount > 0) {

                            messages.add(
                                faultInsertCount
                                + "件の故障情報を登録しました。"
                            );
                        }

                        if (faultReturnUpdateCount > 0) {

                            messages.add(
                                "故障返却フラグを更新しました。"
                            );
                        }

                        if (!messages.isEmpty()) {

                            req.setAttribute(
                                "message",
                                String.join(
                                    " ",
                                    messages
                                )
                            );

                        } else {

                            errors.add(
                                "登録対象がありませんでした。"
                            );
                        }
                    }
                }

                tab = "ownerHistory";
            } else {
                errors.add("不正な操作が行われました。");
                tab = "terminal";
            }

        } catch (Exception e) {
            e.printStackTrace();

            System.out.println(
                "[IPadDetailServlet] ERROR action="
                + action
                + ", id="
                + id
                + ", message="
                + e.getMessage()
            );

            errors.add("処理中にエラーが発生しました。");
            req.setAttribute("errors", errors);
        }

        if (!errors.isEmpty()) {
            req.setAttribute("errors", errors);
        }

        req.setAttribute("tab", tab);
        req.setAttribute("id", id);

        doGet(req, resp);
    }
    private Integer getRoleId(
            HttpSession session) {

        if (session == null) {
            return null;
        }

        Object roleObj =
                session.getAttribute(
                        "authRoleId");

        if (roleObj == null) {
            return null;
        }

        if (roleObj instanceof Number) {
            return ((Number) roleObj).intValue();
        }

        try {

            return Integer.valueOf(
                    roleObj.toString().trim());

        } catch (NumberFormatException e) {

            System.out.println(
                "[IPadDetailServlet] invalid roleId="
                + roleObj
            );

            return null;
        }
    }

    private String trim(String s) {
        return s == null ? null : s.trim();
    }

    private Date parseDate(String s) {
        if (s == null || s.trim().isEmpty()) {
            return null;
        }

        return Date.valueOf(s);
    }
}
