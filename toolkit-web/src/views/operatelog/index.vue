<template>
  <div class="usernamage">
    <div class="usernamage-wrap">
      <div class="search">
        <el-form :inline="true">
          <el-form-item :label="$t('operatelog.operateTime')">
            <div class="block">
              <el-date-picker v-model="tableSearch.value1" class="inputWidth" type="datetime" placeholder="选择日期" size="mini" value-format="yyyy-MM-dd HH:mm:ss" />
              <div style="width: 5px;margin: 0px 10px;">-</div>
              <el-date-picker v-model="tableSearch.value2" class="inputWidth" type="datetime" placeholder="选择日期" size="mini" value-format="yyyy-MM-dd HH:mm:ss" />
            </div>
          </el-form-item>
          <el-form-item :label="$t('operatelog.userName')">
            <el-input v-model.trim="tableSearch.userName" size="mini" class="inputWidth" clearable @keyup.enter.native="search" />
          </el-form-item>
          <el-form-item :label="$t('operatelog.moduleName')">
            <el-input v-model.trim="tableSearch.moduleName" size="mini" class="inputWidth" clearable @keyup.enter.native="search" />
          </el-form-item>
          <el-form-item label-width="1px">
            <el-button size="mini" type="primary" @click="search">{{ $t('common.btnFind') }}</el-button>
            <el-button size="mini" type="primary" @click="reset">{{ $t('common.btnReset') }}</el-button>
          </el-form-item>
        </el-form>
      </div>
      <div class="line" />
      <div class="tabledata">
        <el-table
          ref="table"
          v-loading="loading"
          stripe
          :data="tableData"
          border
          style="width: 100%"
          :header-cell-style="{background:'#E5E9EF',color:'#606266'}"
          :max-height="tableHeight"
        >
          <el-table-column
            :label="$t('operatelog.num')"
            type="index"
            width="60"
            :index="indexMethod"
            align="center"
          />
          <el-table-column prop="userCode" :label="$t('operatelog.userCode')" align="center" width="130">
            <template slot-scope="scope">
              <div class="cutDot" :title="scope.row.userCode">
                {{ scope.row.userCode }}
              </div>
            </template>
          </el-table-column>
          <el-table-column prop="userName" :label="$t('operatelog.userName')" align="center" width="100">
            <template slot-scope="scope">
              <div class="cutDot" :title="scope.row.userName">
                {{ scope.row.userName }}
              </div>
            </template>
          </el-table-column>
          <el-table-column prop="ipAddress" :label="$t('operatelog.ipAddress')" align="center" width="150">
            <template slot-scope="scope">
              <div class="cutDot" :title="scope.row.ipAddress">
                {{ scope.row.ipAddress }}
              </div>
            </template>
          </el-table-column>
          <el-table-column prop="moduleName" align="center" :label="$t('operatelog.moduleName')">
            <template slot-scope="scope">
              <div class="cutDot" :title="scope.row.moduleName">
                {{ scope.row.moduleName }}
              </div>
            </template>
          </el-table-column>
          <el-table-column prop="opName" align="center" :label="$t('operatelog.opName')">
            <template slot-scope="scope">
              <div class="cutDot" :title="scope.row.opName">
                {{ scope.row.opName }}
              </div>
            </template>
          </el-table-column>
          <el-table-column prop="requestTime" :label="$t('operatelog.operateTime')" align="center" width="200">
            <template slot-scope="scope">
              <div class="cutDot" :title="scope.row.requestTime">
                {{ scope.row.requestTime }}
              </div>
            </template>
          </el-table-column>
          <el-table-column prop="resultMsg" :label="$t('operatelog.resultMsg')" align="center" width="100">
            <template slot-scope="scope">
              <div class="cutDot" :title="scope.row.resultMsg">
                {{ scope.row.resultMsg }}
              </div>
            </template>
          </el-table-column>
          <el-table-column :label="$t('common.operat')" fixed="right" width="100" align="center">
            <template slot-scope="scope">
              <el-button type="text" size="mini" @click="lookperson(scope.row)">{{ $t('operatelog.view') }}</el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>
      <div class="page">
        <el-pagination
          background
          :current-page="currentPage"
          :page-sizes="pageSizes"
          :page-size="pageSize"
          layout="total, sizes, prev, pager, next"
          :total="total"
          @current-change="pagechange"
          @size-change="sizeChange"
        />
      </div>
      <!-- 查看 -->
      <el-dialog
        :title="$t('operatelog.view')"
        :visible.sync="lookpersonDia"
        :show-close="false"
        :close-on-click-modal="false"
        :close-on-press-escape="false"
        class="dialog mydialog online-dialog"
        width="40%"
        center
      >
        <el-row type="flex" class="row-bg" justify="center" style="margin-bottom:8px">
          <el-col :span="24">
            <el-row type="flex" class="row-bg">
              <el-col :span="4">{{ $t('operatelog.userCode') }} ：</el-col>
              <el-col :span="20">{{ show.userCode }}</el-col>
            </el-row>
          </el-col>
        </el-row>
        <el-row type="flex" class="row-bg" justify="center" style="margin-bottom:8px">
          <el-col :span="24">
            <el-row type="flex" class="row-bg">
              <el-col :span="4">{{ $t('operatelog.userName') }} ：</el-col>
              <el-col :span="20">{{ show.userName }}</el-col>
            </el-row>
          </el-col>
        </el-row>
        <el-row type="flex" class="row-bg" justify="center" style="margin-bottom:8px">
          <el-col :span="24">
            <el-row type="flex" class="row-bg">
              <el-col :span="4">{{ $t('operatelog.ipAddress') }} ：</el-col>
              <el-col :span="20">{{ show.ipAddress }}</el-col>
            </el-row>
          </el-col>
        </el-row>
        <el-row type="flex" class="row-bg" justify="center" style="margin-bottom:8px">
          <el-col :span="24">
            <el-row type="flex" class="row-bg">
              <el-col :span="4">{{ $t('operatelog.requestUri') }} ：</el-col>
              <el-col :span="20">{{ show.requestUri }}</el-col>
            </el-row>
          </el-col>
        </el-row>
        <el-row type="flex" class="row-bg" justify="center" style="margin-bottom:8px">
          <el-col :span="24">
            <el-row type="flex" class="row-bg">
              <el-col :span="4">{{ $t('operatelog.moduleName') }} ：</el-col>
              <el-col :span="20">{{ show.moduleName }}</el-col>
            </el-row>
          </el-col>
        </el-row>
        <el-row type="flex" class="row-bg" justify="center" style="margin-bottom:8px">
          <el-col :span="24">
            <el-row type="flex" class="row-bg">
              <el-col :span="4">{{ $t('operatelog.opName') }} ：</el-col>
              <el-col :span="20">{{ show.opName }}</el-col>
            </el-row>
          </el-col>
        </el-row>
        <el-row type="flex" class="row-bg" justify="center" style="margin-bottom:8px">
          <el-col :span="24">
            <el-row type="flex" class="row-bg">
              <el-col :span="4">{{ $t('operatelog.className') }} ：</el-col>
              <el-col :span="20" style="text-align:left">{{ show.className }}</el-col>
            </el-row>
          </el-col>
        </el-row>
        <el-row type="flex" class="row-bg" justify="center" style="margin-bottom:8px">
          <el-col :span="24">
            <el-row type="flex" class="row-bg">
              <el-col :span="4">{{ $t('operatelog.methodName') }} ：</el-col>
              <el-col :span="20" style="text-align:left">{{ show.methodName }}</el-col>
            </el-row>
          </el-col>
        </el-row>
        <el-row type="flex" class="row-bg" justify="center" style="margin-bottom:8px">
          <el-col :span="24">
            <el-row type="flex" class="row-bg">
              <el-col :span="4">{{ $t('operatelog.resultCode') }} ：</el-col>
              <el-col :span="20" style="text-align:left">{{ show.resultCode }}</el-col>
            </el-row>
          </el-col>
        </el-row>
        <el-row type="flex" class="row-bg" justify="center" style="margin-bottom:8px">
          <el-col :span="24">
            <el-row type="flex" class="row-bg">
              <el-col :span="4">{{ $t('operatelog.resultMsg') }} ：</el-col>
              <el-col :span="20" style="text-align:left">{{ show.resultMsg }}</el-col>
            </el-row>
          </el-col>
        </el-row>
        <el-row type="flex" class="row-bg" justify="center" style="margin-bottom:8px">
          <el-col :span="24">
            <el-row type="flex" class="row-bg">
              <el-col :span="4">{{ $t('operatelog.operateTime') }} ：</el-col>
              <el-col :span="20" style="text-align:left">{{ show.requestTime }}</el-col>
            </el-row>
          </el-col>
        </el-row>
        <el-row type="flex" class="row-bg" justify="center" style="margin-bottom:8px">
          <el-col :span="24">
            <el-row type="flex" class="row-bg">
              <el-col :span="4">{{ $t('operatelog.execTime') }} ：</el-col>
              <el-col :span="20" style="text-align:left">{{ show.execTime }}</el-col>
            </el-row>
          </el-col>
        </el-row>
        <el-row type="flex" class="row-bg" justify="center" style="margin-bottom:8px">
          <el-col :span="24">
            <el-row type="flex" class="row-bg">
              <el-col :span="4">{{ $t('operatelog.requestBody') }} ：</el-col>
              <el-col :span="20" style="text-align:left">{{ show.requestBody }}</el-col>
            </el-row>
          </el-col>
        </el-row>
        <el-row type="flex" class="row-bg" justify="center" style="margin-bottom:8px">
          <el-col :span="24">
            <el-row type="flex" class="row-bg">
              <el-col :span="4">{{ $t('operatelog.requestHeaders') }} ：</el-col>
              <el-col :span="20" style="text-align:left">{{ show.requestHeaders }}</el-col>
            </el-row>
          </el-col>
        </el-row>
        <el-row type="flex" class="row-bg" justify="center" style="margin-bottom:8px">
          <el-col :span="24">
            <el-row type="flex" class="row-bg">
              <el-col :span="4">{{ $t('operatelog.exceptionStacktrace') }} ：</el-col>
              <el-col :span="20" style="text-align:left">{{ show.exceptionStacktrace }}</el-col>
            </el-row>
          </el-col>
        </el-row>
        <el-row type="flex" class="row-bg" justify="center" style="margin-bottom:8px">
          <el-col :span="24">
            <el-row type="flex" class="row-bg">
              <el-col :span="4">{{ $t('operatelog.remarks') }} ：</el-col>
              <el-col :span="20" style="text-align:left">{{ show.remarks }}</el-col>
            </el-row>
          </el-col>
        </el-row>
        <span slot="footer" class="dialog-footer">
          <el-button size="mini" type="info" @click="lookpersonDia = false">{{ $t('common.btnClose') }}</el-button>
        </span>
      </el-dialog>
    </div>
  </div>
</template>

<script>
import { getOpLogInfo } from '@/api/operatelog'
export default {
  data() {
    return {
      total: 0,
      totalint: 1,
      tableHeight: 0,
      pageSizes: [10, 20, 30, 40],
      pageSize: 20,
      InsertCenterDialogVisible: false,
      dialog: false,
      lookpersonDia: false,
      currentPage: 1,
      dateSelect: '',
      table: {
        value1: '',
        value2: '',
        userName: '',
        moduleName: ''
      },
      tableSearch: {
        value1: '',
        value2: '',
        userName: '',
        moduleName: ''
      },
      tableData: [],
      outData: [],
      row: {
        className: '',
        exceptionStacktrace: '',
        execTime: '',
        id: '',
        ipAddress: '',
        methodName: '',
        moduleName: '',
        opName: '',
        remarks: '',
        requestBody: '',
        requestHeaders: '',
        requestTime: '',
        requestTimeFrom: [],
        requestTimeTo: [],
        requestUri: '',
        resultCode: '',
        resultMsg: '',
        userCode: '',
        userName: ''
      },
      show: {
        className: '',
        exceptionStacktrace: '',
        execTime: '',
        id: '',
        ipAddress: '',
        methodName: '',
        moduleName: '',
        opName: '',
        remarks: '',
        requestBody: '',
        requestHeaders: '',
        requestTime: '',
        requestTimeFrom: [],
        requestTimeTo: [],
        requestUri: '',
        resultCode: '',
        resultMsg: '',
        userCode: '',
        userName: ''
      },
      // buttonList: [],
      loading: false
    }
  },
  created() {
    const screenHeight = document.documentElement.clientHeight - 200 + 'px'
    this.$nextTick(() => {
      this.tableHeight = screenHeight
    })
  },
  mounted() {
    this.init()
    const that = this
    window.onresize = () => {
      return (() => {
        const screenHeight = document.documentElement.clientHeight - 200 + 'px'
        that.tableHeight = screenHeight
      })()
    }
  },
  destroyed() {
    window.onresize = null
  },
  methods: {
    indexMethod(index) {
      return (~~this.currentPage - 1) * this.pageSize + index + 1
    },
    pagechange(e) {
      this.tableData = []
      this.loading = true
      this.currentPage = e
      this.init()
    },
    // 检索
    search() {
      this.tableData = []
      this.loading = true
      this.currentPage = 1
      this.init()
    },
    main(data) {
      data.size = this.pageSize
      // handleLoading.call(this, true)
      if (typeof this.tableSearch.value1 === 'object') {
        data.requestTimeFrom = ''
      }
      if (typeof this.tableSearch.value2 === 'object') {
        data.requestTimeTo = ''
      }
      getOpLogInfo(data).then(res => {
        this.total = res.total
        this.tableData = res.records
        // handleLoading.call(this)
        this.loading = false
      })
    },
    // 初始化
    init() {
      const par = {
        current: this.currentPage,
        userName: this.tableSearch.userName,
        moduleName: this.tableSearch.moduleName,
        requestTimeFrom: this.tableSearch.value1,
        requestTimeTo: this.tableSearch.value2,
        size: this.pageSize
      }
      this.main(par)
    },
    // 重置
    reset() {
      this.tableSearch.value1 = ''
      this.tableSearch.value2 = ''
      this.tableSearch.userName = ''
      this.tableSearch.moduleName = ''
    },
    // 查看
    lookperson(e) {
      this.lookpersonDia = true
      this.show = e
    },
    sizeChange(val) {
      this.loading = true
      this.tableData = []
      this.pageSize = val
      this.init(1)
    }
  }
}

</script>

<style type="less" scoped>
.usernamage{text-align: left;width: 98%;height: 100%;margin: 0 auto;overflow: hidden;font-size: 15px;}
.usernamage-wrap{
  height: 100%;
  background: #ffffff;
  border-radius: 6px;
  overflow: hidden; }
  .tabledata{
    padding: 5px 10px 0;
  }
.search{
  margin: 0 !important;
  padding: 5px 0;
  display: flex;
  align-items: center;
  background-color: #fff;
}
.search .el-form .el-form-item{
  margin: 0 0 0 10px !important;
}
.search .el-form .el-form-item /deep/ .el-form-item__label{
    height: 30px;
    line-height: 30px;
    font-weight:normal !important;
}
.search .el-form .el-form-item /deep/ .el-form-item__content{
    height: 30px;
    line-height: 30px;
}

@media screen and (max-width: 1366px) {
  .search .el-form .el-form-item /deep/ .el-form-item__label{
      height: 23px;
      line-height: 23px;
  }
  .search .el-form .el-form-item /deep/ .el-form-item__content{
      height: 23px;
      line-height: 23px;
  }
  .search .el-form .el-form-item /deep/ .el-form-item__content .el-input__icon{
      line-height: 24px;
  }
  .search .el-form .el-form-item /deep/ .el-form-item__content .el-input__inner{
      height: 23px !important;
      line-height: 24px !important;
      vertical-align: middle;
  }
  .search .el-form .el-form-item /deep/ .el-form-item__content button{
      height: 23px !important;
      line-height: 23px !important;
      padding: 0 10px !important;
  }
  .search .el-form .el-form-item /deep/ .el-form-item__content button+button{
    margin-left: 7px;
  }
}
 .page {background:#fff;text-align: right;text-indent: initial;margin-top: 5px;}
 .usernamage /deep/ .el-dialog__body {padding: 0 20px;}
 .usernamage /deep/ .el-button--text{padding: 0;}
  .usernamage /deep/ .el-form-item{margin-bottom: 0;}
  .tablepage{text-align: right;}
  .padingb10{padding-bottom: 18px;}
   .usernamage  /deep/ .el-form-item__label{ text-align: right;
    text-align-last: inherit;}
    .tabledata /deep/ .el-button--text{padding: 0;}
  .usernamage /deep/ .el-table td, .el-table th{padding: 3px 0;}
  .block{display: flex;}
  .usernamage{
    padding: 10px;
  }
</style>
