<template>
  <div class="usernamage">
    <router-view />
    <div class="usernamage-wrap">
      <div class="search">
        <el-form :inline="true">
          <el-form-item :label="$t('devopstools.toolName')">
            <el-input v-model.trim="toolForm.templateName" size="mini" class="inputWidth" />
          </el-form-item>
          <el-form-item label-width="1px">
            <el-button type="primary" size="mini" @click="selectList()">{{ $t('common.btnFind') }}</el-button>
            <el-button type="primary" size="mini" @click="resetList()">{{ $t('common.btnReset') }}</el-button>
            <el-button type="primary" size="mini" @click="showDialogVisible()">{{ $t('devopstools.import') }}</el-button>
          </el-form-item>
        </el-form>
      </div>
      <div class="line" />
      <div class="tabledata">
        <el-table
          ref="multipleTable"
          v-loading="listLoading"
          :data="list"
          element-loading-text="Loading"
          style="width: 100%;"
          :max-height="tableHeight"
          border
          stripe
          :header-cell-style="{background:'#E5E9EF',color:'#606266'}"
        >
          <el-table-column type="index" align="center" :index="indexMethod" :label="$t('devopstools.num')" width="60" />
          <el-table-column align="center" :label="$t('devopstools.toolName')" min-width="250">
            <template v-slot="scope">
              {{ scope.row.devopsName }}
            </template>
          </el-table-column>
          <el-table-column align="center" :label="$t('devopstools.description')" min-width="350">
            <template v-slot="scope">
              {{ scope.row.deptdesc }}
            </template>
          </el-table-column>
          <el-table-column align="center" :label="$t('devopstools.importTime')" min-width="200">
            <template v-slot="scope">
              {{ scope.row.createTime }}
            </template>
          </el-table-column>
          <el-table-column align="center" fixed="right" :label="$t('common.operat')" min-width="150">
            <template v-slot="scope">
              <el-button type="text" style="margin-left: 10px;" size="mini" @click="goView(scope.row.id)"> {{ $t('devopstools.view') }} </el-button>
              <el-button type="text" style="margin-left: 10px;" size="mini" @click="goEdit(scope.row.id)"> {{ $t('devopstools.edit') }} </el-button>
              <el-button type="text" style="margin-left: 10px;color:#F00;" size="mini" @click="deleteDate(scope.row.id)"> {{ $t('common.delete') }} </el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>
      <div class="page">
        <el-pagination
          background
          :current-page="toolForm.pageNum"
          :page-sizes="[10, 20, 30, 40]"
          :page-size="toolForm.pageSize"
          layout="total, sizes, prev, pager, next"
          :total="listSize"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
      <el-dialog
        :visible.sync="dialogVisible"
        :title="$t('devopstools.import')"
        width="30%"
      >
        <el-form label-width="25%">
          <el-form-item :label="$t('devopstools.toolName')">
            <el-input v-model="toolName" size="mini" style="width: 80%;" />
          </el-form-item>
          <el-form-item :label="$t('devopstools.description')">
            <el-input v-model="description" size="mini" type="textarea" :rows="3" style="width: 80%;" />
          </el-form-item>
          <el-form-item :label="$t('devopstools.selectFile')">
            <div>
              <el-input v-model="fileName" size="mini" disabled style="width: 80%; display: inline-block;" />
              <el-button size="mini" style="margin-left: 10px;" @click="fileSelect()">...</el-button>
              <input ref="fileagain" type="file" style="display: none;" class="upfile" name="file" accept=".xml" @change="changeInput()">
            </div>
          </el-form-item>
        </el-form>
        <div slot="footer" style="text-align:right">
          <el-button type="primary" size="mini" @click="uploadFile()">{{ $t('common.btnConfirm') }}</el-button>
          <el-button size="mini" @click="dialogVisible = false">{{ $t('common.btnCancel') }}</el-button>
        </div>
      </el-dialog>
    </div>
  </div>
</template>

<script>
import { getByPage, importTemplate, deleteTool } from '@/api/devops.js'
export default {
  data() {
    return {
      listLoading: true,
      list: [],
      tableHeight: 0,
      listSize: 0,
      toolForm: {
        templateName: '',
        pageNum: 1,
        pageSize: 20
      },
      dialogVisible: false,
      fileName: '',
      toolName: '',
      description: ''
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
      return (~~this.toolForm.pageNum - 1) * this.toolForm.pageSize + index + 1
    },
    // 页面打开
    selectList() {
      this.listLoading = true
      getByPage(this.toolForm).then(res => {
        if (res.data && res.code === 0) {
          this.list = res.data.records
          this.listSize = res.data.total
        }
      }).finally(() => {
        this.listLoading = false
      })
    },
    // 重置
    resetList() {
      this.toolForm.templateName = ''
      // 重置翻页组件数据
      this.toolForm.pageNum = 1
      this.toolForm.pageSize = 20
      this.selectList()
    },
    // 删除工具
    deleteDate(data) {
      // 是否删除？
      this.$confirm(this.$t('common.delConfirm'), this.$t('common.prompt'), {
        // 确认
        confirmButtonText: this.$t('common.btnConfirm'),
        // 取消
        cancelButtonText: this.$t('common.btnCancel'),
        cancelButtonClass: 'bhh-btn-custom-cancel'
      }).then(async() => {
        this.listLoading = true
        deleteTool(data).then(res => {
          // 重置翻页组件数据
          this.toolForm.pageNum = 1
          this.toolForm.pageSize = 20
          this.selectList()
          if (res.code === 0) {
            this.$message({
              type: 'success',
              message: this.$t('common.deleteSuccess'),
              duration: 1000
            })
          }
        }).finally(() => {
          this.listLoading = false
        })
      })
    },
    // 修改每页展示多少条数据
    handleSizeChange(size) {
      this.toolForm.pageSize = size
      this.selectList()
    },
    // 翻页
    handleCurrentChange(page) {
      this.toolForm.pageNum = page
      this.selectList()
    },
    // 打开选择文件页面
    fileSelect() {
      this.$refs.fileagain.click()
    },
    // 选择文件显示文件名
    changeInput() {
      const file = this.$refs.fileagain.files[0]
      this.fileName = file.name
    },
    // 文件
    uploadFile() {
      // 工具名称 check
      if (this.toolName === '') {
        this.$message({
          type: 'warning',
          message: this.$t('devopstools.toolNameNull'),
          duration: 1000
        })
        return
      }

      // 是否选中文件
      if (this.$refs.fileagain.files.length === 0) {
        this.$message({
          type: 'warning',
          message: this.$t('devopstools.editNull'),
          duration: 1000
        })
        return
      }

      // 是否上传？
      this.$confirm(this.$t('devopstools.uplodeConfirm'), this.$t('common.prompt'), {
        // 确认
        confirmButtonText: this.$t('common.btnConfirm'),
        // 取消
        cancelButtonText: this.$t('common.btnCancel'),
        cancelButtonClass: 'bhh-btn-custom-cancel'
      }).then(async() => {
        this.listLoading = true
        const formData = new FormData()
        formData.append('devopsName', this.toolName)
        formData.append('deptdesc', this.description)
        formData.append('xml', this.$refs.fileagain.files[0])
        formData.append('fileName', this.fileName)
        importTemplate(formData).then(res => {
          if (res.code === 0) {
            // 重置翻页组件数据
            this.toolForm.pageNum = 1
            this.toolForm.pageSize = 20
            this.selectList()
            this.$message({
              type: 'success',
              message: this.$t('devopstools.uplodeSuccess'),
              duration: 1000
            })
          }
        }).finally(() => {
          this.listLoading = false
          this.dialogVisible = false
        })
      })
    },
    // 打开文件上传页面
    showDialogVisible() {
      this.toolName = ''
      this.description = ''
      this.fileName = ''
      if (this.$refs.fileagain !== undefined) {
        this.$refs.fileagain.value = ''
      }
      this.dialogVisible = true
    },
    // 查看
    goView(data) {
      this.$router.push({
        name: 'view',
        params: {
          devopsId: data
        }
      })
    },
    goEdit(data) {
      this.$router.push({
        name: 'edit',
        params: {
          devopsId: data
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
