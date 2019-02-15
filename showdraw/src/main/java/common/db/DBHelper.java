package common.db;

import android.content.Context;

import com.weqia.utils.L;
import com.weqia.utils.StrUtil;
import com.weqia.utils.bitmap.DiscussChildLoadData;
import com.weqia.utils.bitmap.DiscussShowData;
import com.weqia.utils.bitmap.LoadErrData;
import com.weqia.utils.data.LocalNetPath;
import com.weqia.utils.datastorage.db.DaoConfig;
import com.weqia.utils.datastorage.db.DbUtil;
import com.weqia.utils.exception.CheckedExceptionHandler;


import java.util.HashMap;

import common.WeqiaApplication;

public class DBHelper {

    /**
     * @author chenjh 创建所有表
     */
    public static void createAllTable(Context ctx) {
        try {
            L.i("创建所有表");
 /*           if (WeqiaApplication.getInstance().getLoginUser() != null) {
                DaoConfig config = new DaoConfig();
                config.setContext(WeqiaApplication.ctx);
                config.setDbName(WeqiaApplication.getInstance().getLoginUser()
                        .getMid());
                final WeqiaDbUtil util = WeqiaDbUtil.create(config);
                WeqiaApplication.getInstance().setDbUtil(util);

                util.CreatTable(LocalNetPath.class);
                util.CreatTable(LoadErrData.class);
                util.CreatTable(DownContactErr.class);
                util.CreatTable(DownloadInfo.class);
                util.CreatTable(CacheEntity.class);
                util.CreatTable(DiscussShowData.class);
                util.CreatTable(DiscussChildLoadData.class);

                util.CreatTable(PushUniqueData.class);
                util.CreatTable(AttachmentData.class);
                util.CreatTable(NotifyData.class);
                util.CreatTable(SilenceData.class);
                util.CreatTable(LinksData.class);

                util.CreatTable(UpAttachData.class);
                util.CreatTable(WaitSendData.class);
                util.CreatTable(WaitUpFileData.class);

                RouterUtil.routerActionSync(WeqiaApplication.ctx, null, "pvmain", "acglobal", BaseModeEnum.DB.value(), DBOpEnum.CRATETABLE.value());
            }*/
            DaoConfig config = new DaoConfig();
            config.setContext(ctx);
            config.setDbName("123");
            final WeqiaDbUtil util = WeqiaDbUtil.create(config);
            WeqiaApplication.getInstance().setDbUtil(util);

            util.CreatTable(LocalNetPath.class);
            util.CreatTable(LoadErrData.class);
        } catch (Exception e) {
            CheckedExceptionHandler.handleException(e);
        }

    }

    public static void clearAllTable() {
        try {
          /*  if (WeqiaApplication.getInstance().getDbUtil() != null) {
                final DbUtil util = WeqiaApplication.getInstance().getDbUtil();

                util.clear(LocalNetPath.class);
                util.clear(LoadErrData.class);
                util.clear(DownContactErr.class);
                util.clear(DownloadInfo.class);
                util.clear(CacheEntity.class);
                util.clear(DiscussShowData.class);
                util.clear(DiscussChildLoadData.class);

                util.clear(PushUniqueData.class);
                util.clear(AttachmentData.class);
                util.clear(NotifyData.class);
                util.clear(LinksData.class);

                util.clear(UpAttachData.class);
                util.clear(WaitSendData.class);
                util.clear(WaitUpFileData.class);

                RouterUtil.routerActionSync(WeqiaApplication.ctx, null, "pvmain", "acglobal", BaseModeEnum.DB.value(), DBOpEnum.CLEARTABLE.value());
            }*/
        } catch (Exception e) {
            CheckedExceptionHandler.handleException(e);
        }
    }

    public static void clearCoTable(String coId) {
        if (StrUtil.isEmptyOrNull(coId)) {
            return;
        }
        try {
 /*           if (WeqiaApplication.getInstance().getDbUtil() != null) {

                final DbUtil util = WeqiaApplication.getInstance().getDbUtil();

                util.clear(LoadErrData.class);
                util.clear(DownContactErr.class);
                util.clear(DownloadInfo.class);
                util.clear(CacheEntity.class);
                util.clear(DiscussShowData.class);
                util.clear(DiscussChildLoadData.class);
                util.deleteByWhere(PushUniqueData.class, "gCoId='" + coId + "'");
                util.deleteByWhere(AttachmentData.class, "gCoId='" + coId + "'");
                util.clear(LinksData.class);
                util.clear(LocalNetPath.class);

                util.clear(UpAttachData.class);
                util.clear(WaitSendData.class);
                util.clear(WaitUpFileData.class);

                HashMap<String, String> dataMap = new HashMap<>();
                dataMap.put(BaseModeEnum.DB.value(), DBOpEnum.CLEARBYCO.value());
                dataMap.put(ModulesConstants.ROUTER_PARAM, coId);
                RouterUtil.routerActionSync(WeqiaApplication.ctx, null, "pvmain", "acglobal", dataMap);
            }*/
        } catch (Exception e) {
            CheckedExceptionHandler.handleException(e);
        }
    }
}
