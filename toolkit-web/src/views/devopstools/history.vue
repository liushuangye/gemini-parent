<template>
  <div class="usernamage">
    <router-view />
    <div class="usernamage-wrap">
      <div class="line" />
      <div class="tabledata">
        <el-table
          ref="multipleTable"
          v-loading="listLoading"
          :data="list"
          stripe
          element-loading-text="Loading"
          style="width: 100%"
          :max-height="tableHeight"
          :header-cell-style="{background:'#E5E9EF',color:'#606266'}"
          border
        >
          <el-table-column type="index" align="center" :label="$t('devopstools.num')" width="60" />
          <el-table-column align="center" :label="$t('devopstools.step')" min-width="100">
            <template v-slot="scope">
              {{ scope.row.stepName }}
            </template>
          </el-table-column>
          <el-table-column align="center" :label="$t('devopstools.description')" min-width="200">
            <template v-slot="scope">
              {{ scope.row.stepDesc }}
            </template>
          </el-table-column>
          <el-table-column align="center" :label="$t('devopstools.execParamsStr')" min-width="80">
            <template v-slot="scope">
              <el-button type="text" size="mini" @click="view(scope.row.execParamsStr, $t('devopstools.execParamsStr'))">{{ $t('devopstools.view') }}</el-button>
            </template>
          </el-table-column>
          <el-table-column align="center" :label="$t('devopstools.execSqlStr')" min-width="80">
            <template v-slot="scope">
              <el-button v-if="scope.row.execSqlStr!=null" type="text" size="mini" @click="view(scope.row.execSqlStr, $t('devopstools.execSqlStr'))">{{ $t('devopstools.view') }}</el-button>
            </template>
          </el-table-column>
          <el-table-column align="center" :label="$t('devopstools.execDataStr')" min-width="80">
            <template v-slot="scope">
              <el-button v-if="scope.row.execDataStr!=null" type="text" size="mini" @click="view(scope.row.execDataStr, $t('devopstools.execDataStr'))">{{ $t('devopstools.view') }}</el-button>
            </template>
          </el-table-column>
          <el-table-column align="center" :label="$t('devopstools.backUpDataStr')" min-width="80">
            <template v-slot="scope">
              <el-button v-if="scope.row.backUpDataStr!=null" type="text" size="mini" @click="view(scope.row.backUpDataStr, $t('devopstools.backUpDataStr'))">{{ $t('devopstools.view') }}</el-button>
            </template>
          </el-table-column>
          <el-table-column align="center" :label="$t('devopstools.rollBackSqlStr')" min-width="80">
            <template v-slot="scope">
              <el-button v-if="scope.row.rollBackSqlStr!=null" type="text" size="mini" @click="view(scope.row.rollBackSqlStr, $t('devopstools.rollBackSqlStr'))">{{ $t('devopstools.view') }}</el-button>
            </template>
          </el-table-column>
          <el-table-column align="center" :label="$t('devopstools.status')" min-width="80">
            <template v-slot="scope">
              {{ scope.row.status }}
            </template>
          </el-table-column>
          <el-table-column align="center" :label="$t('devopstools.userName')" min-width="80">
            <template v-slot="scope">
              {{ scope.row.userName }}
            </template>
          </el-table-column>
          <el-table-column align="center" :label="$t('devopstools.createDateTime')" min-width="180">
            <template v-slot="scope">
              {{ scope.row.createDateTime }}
            </template>
          </el-table-column>
        </el-table>
      </div>
      <el-dialog
        :visible.sync="dialogVisible"
        :title="title"
      >
        <json-viewer :value="jsonData" :expand-depth="2" />
      </el-dialog>
    </div>
    <el-button type="primary" style="margin-left: 50%;margin-top: 10px;" @click="btnReturn()">{{ $t('common.btnReturn') }}</el-button>
  </div>
</template>

<script>
import { getDetails } from '@/api/devops.js'
export default {
  data() {
    return {
      listLoading: true,
      list: [],
      tableHeight: 0,
      jsonData: {},
      dialogVisible: false,
      title: ''
    }
  },
  created() {
    const screenHeight = document.documentElement.clientHeight - 150 + 'px'
    this.$nextTick(() => {
      this.tableHeight = screenHeight
    })
  },
  mounted() {
    this.selectList()
    const that = this
    window.onresize = () => {
      return (() => {
        const screenHeight = document.documentElement.clientHeight - 150 + 'px'
        that.tableHeight = screenHeight
      })()
    }
  },
  methods: {
    view(data, str) {
      this.title = str
      this.dialogVisible = true
      this.jsonData = JSON.parse(data)
    },
    selectList() {
      this.listLoading = true
      getDetails(this.$route.params.taskId).then(res => {
        if (res.data && res.code === 0) {
          this.list = res.data.stepList
        }
      }).finally(() => {
        this.listLoading = false
      })
    },
    btnReturn() {
      this.$router.push({ path: '/devopstools/log' })
    }
  }
}
</script>

<style scoped>
.usernamage{text-align: left;width: 98%;height: 100%;margin: 0 auto;overflow: hidden;font-size: 15px;}
.usernamage-wrap{height: 100%;background: #ffffff;border-radius: 6px;overflow: hidden;}
.usernamage /deep/ .el-dialog__body {padding: 0 20px;}
.usernamage /deep/ .el-button--text{padding: 0;}
.usernamage /deep/ .el-form-item{margin-bottom: 0;}
.padingb10{padding-bottom: 18px;}
.usernamage  /deep/ .el-form-item__label{ text-align: right;text-align-last: inherit;}
.tabledata /deep/ .el-button--text{padding: 0;}
.usernamage /deep/ .el-table td, .el-table th{padding: 3px 0;}
.block{display: flex;}
.usernamage{padding: 10px;}
</style>
