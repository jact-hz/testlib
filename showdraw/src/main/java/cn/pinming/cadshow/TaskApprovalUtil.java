package cn.pinming.cadshow;

import android.app.Activity;
import android.app.Dialog;
import android.view.View;

import com.weqia.utils.StrUtil;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import common.GlobalConstants;
import common.request.ResultEx;
import common.request.ServiceParams;
import common.request.ServiceRequester;
import common.request.UserService;

/**
 * Created by lgf on 2019/1/12.
 */

public class TaskApprovalUtil {
    public static Dialog taskDialog;
/*
    public static void toTask(final Activity context, String pjId, final HashMap<String, String> dataMap) {

        ServiceParams params = new ServiceParams(1013);
        params.setSize(100);
        params.setPjId(pjId);
        UserService.getDataFromServer(params, new ServiceRequester() {
            @Override
            public void onResult(ResultEx resultEx) {
                if (resultEx.isSuccess()) {
                    List<FormListData> list = resultEx.getDataArray(FormListData.class);
                    if (StrUtil.listNotNull(list)) {
                        List<String> flowName = new ArrayList<>();
                        List<String> flowId = new ArrayList<>();
                        for (FormListData formListData : list) {
                            if (StrUtil.notEmptyOrNull(formListData.getFlowName())) {
                                flowName.add(formListData.getFlowName());
                            }
                            if (StrUtil.notEmptyOrNull(formListData.getFlowId())) {
                                flowId.add(formListData.getFlowId());
                            }
                        }
                        String[] flowNameArr = new String[flowName.size()];
                        final String[] flowIdArr = flowId.toArray(new String[flowId.size()]);
                        taskDialog = DialogUtil.initLongClickDialog(context, "任务类型", flowName.toArray(flowNameArr), new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                taskDialog.dismiss();
                                String key = (String) view.getTag(-1);
                                int i = (int) view.getTag();
//                                Intent intent = new Intent(context, ApprovalVoiceNewActivity.class);
//                                Intent intent = new Intent();
//                                intent.putExtra(GlobalConstants.KEY_TOP_BANNER_TITLE, key);
//                                intent.putExtra("flowId", flowIdArr[i]);
//                                context.startActivity(intent);
                                HashMap<String, String> pinMap = new HashMap<>();
                                pinMap.putAll(dataMap);
                                pinMap.put(GlobalConstants.KEY_TOP_BANNER_TITLE, key);
                                pinMap.put("flowId", flowIdArr[i]);
                                ShowDrawUtil.ronterActionSync(context, null, "pvapproval", "acnewapproval", pinMap);
//                                context.startToActivity(ApprovalVoiceNewActivity.class, key, flowIdArr[0]);
                            }
                        });
                        taskDialog.show();
                    }
                }
            }
        });
*//*        final String[] list = {"安全任务","质量任务","进度任务","其他任务"};
        taskDialog = DialogUtil.initLongClickDialog(context, "任务类型", list, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                taskDialog.dismiss();
                String key = (String) view.getTag(-1);
                int i = (int) view.getTag();
                context.startToActivity(ApprovalVoiceNewActivity.class, key);
            }
        });
        taskDialog.show();*//*
    }*/
}
