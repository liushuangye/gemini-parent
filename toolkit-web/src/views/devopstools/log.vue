<template>
  <div class="usernamage">
    <router-view />
    <div class="usernamage-wrap">
      <div class="search">
        <el-form :inline="true">
          <el-form-item :label="$t('devopstools.taskName')">
            <el-input v-model.trim="taskForm.templateName" size="mini" class="inputWidth" />
          </el-form-item>
          <el-form-item :label="$t('devopstools.createTime')">
            <div class="block">
              <el-date-picker v-model="taskForm.startDate" class="inputWidth" type="datetime" placeholder="选择日期" size="mini" value-format="yyyy-MM-dd HH:mm:ss" />
              <div style="width: 5px;margin: 0px 10px;">-</div>
              <el-date-picker v-model="taskForm.endDate" class="inputWidth" type="datetime" placeholder="选择日期" size="mini" value-format="yyyy-MM-dd HH:mm:ss" />
            </div>
          </el-form-item>
          <el-form-item label-width="1px">
            <el-button type="primary" size="mini" @click="selectList()">{{ $t('common.btnFind') }}</el-button>
            <el-button type="primary" size="mini" @click="resetList()">{{ $t('common.btnReset') }}</el-button>
          </el-form-item>
        </el-form>
      </div>
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
          <el-table-column type="index" align="center" :index="indexMethod" :label="$t('devopstools.num')" width="60" />
          <el-table-column align="center" :label="$t('devopstools.taskName')" min-width="200">
            <template v-slot="scope">
              {{ scope.row.taskName }}
            </template>
          </el-table-column>
          <el-table-column align="center" :label="$t('devopstools.toolName')" min-width="200">
            <template v-slot="scope">
              {{ scope.row.devopsName }}
            </template>
          </el-table-column>
          <el-table-column align="center" :label="$t('devopstools.createName')" min-width="150">
            <template v-slot="scope">
              {{ scope.row.createUser }}
            </template>
          </el-table-column>
          <el-table-column align="center" :label="$t('devopstools.createTime')" min-width="180">
            <template v-slot="scope">
              {{ scope.row.createTime }}
            </template>
          </el-table-column>
          <el-table-column align="center" fixed="right" :label="$t('common.operat')" min-width="100">
            <template v-slot="scope">
              <el-button type="text" size="mini" @click="goDetail(scope.row.id)"> {{ $t('devopstools.view') }} </el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>
      <div class="page">
        <el-pagination
          background
          :current-page="taskForm.pageNum"
          :page-sizes="[10, 20, 30, 40]"
          :page-size="taskForm.pageSize"
          layout="total, sizes, prev, pager, next"
          :total="listSize"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </div>
  </div>
</template>

<script>
import { getByPageTask } from '@/api/devops.js'
export default {
  data() {
    return {
      listLoading: true,
      list: [],
      listSize: 0,
      tableHeight: 0,
      taskForm: {
        templateName: '',
        pageNum: 1,
        pageSize: 20,
        startDate: '',
        endDate: ''
      }
    }
  },
  created() {
    const screenHeight = document.documentElement.clientHeight - 200 + 'px'
    this.$nextTick(() => {
      this.tableHeight = screenHeight
    })
  },
  mounted() {
    this.selectList()
    const that = this
    window.onresize = () => {
      return (() => {
        const screenHeight = document.documentElement.clientHeight - 200 + 'px'
        that.tableHeight = screenHeight
      })()
    }
  },
  methods: {
    indexMethod(index) {
      return (~~this.taskForm.pageNum - 1) * this.taskForm.pageSize + index + 1
    },
    selectList() {
      this.listLoading = true
      getByPageTask(this.taskForm).then(res => {
        if (res.data && res.code === 0) {
          this.list = res.data.records
          this.listSize = res.data.total
        }
      }).finally(() => {
        this.listLoading = false
      })
    },
    // 修改每页展示多少条数据
    handleSizeChange(size) {
      this.taskForm.pageSize = size
      this.selectList()
    },
    // 翻页
    handleCurrentChange(page) {
      this.taskForm.pageNum = page
      this.selectList()
    },
    // 重置
    resetList() {
      this.taskForm.templateName = ''
      this.taskForm.startDate = ''
      this.taskForm.endDate = ''
      // 重置翻页组件数据
      this.taskForm.pageNum = 1
      this.taskForm.pageSize = 20
      this.selectList()
    },
    // 查看
    goDetail(data) {
      this.$router.push({
        name: 'history',
        params: {
          taskId: data
        }
      })
    }
  }
}
</script>

<style scoped>
.usernamage{text-align: left;width: 98%;height: 100%;margin: 0 auto;overflow: hidden;font-size: 15px;}
.usernamage-wrap{height: 100%;background: #ffffff;border-radius: 6px;overflow: hidden;}
.search{margin: 0 !important;padding: 5px 0;display: flex;align-items: center;background-color: #fff;}
.search .el-form .el-form-item{margin: 0 0 0 10px !important;}
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
.usernamage  /deep/ .el-form-item__label{ text-align: right;text-align-last: inherit;}
.tabledata /deep/ .el-button--text{padding: 0;}
.usernamage /deep/ .el-table td, .el-table th{padding: 3px 0;}
.block{display: flex;}
.usernamage{padding: 10px;}
</style>
