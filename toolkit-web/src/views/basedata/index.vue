<template>
  <div class="usernamage">
    <div class="usernamage-wrap">
      <div class="search">
        <el-form :inline="true">
          <el-form-item>
            <el-select v-model="value" size="mini" @change="selectChange()">
              <el-option v-for="item in options" :key="item.value" size="mini" :label="item.label" :value="item.value" />
            </el-select>
          </el-form-item>
          <el-form-item label-width="1px">
            <el-button type="primary" size="mini" :disabled="btnDisabled" @click="downloadTemplate()">{{ $t('basedata.downloadTemplate') }}</el-button>
            <el-button type="primary" size="mini" @click="showDialogVisible()">{{ $t('basedata.importTemplate') }}</el-button>
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
          <el-table-column type="index" align="center" :index="indexMethod" :label="$t('basedata.num')" width="60" />
          <el-table-column align="center" :label="$t('basedata.templateName')" min-width="250">
            <template v-slot="scope">
              {{ scope.row.tempalteName }}
            </template>
          </el-table-column>
          <el-table-column align="center" :label="$t('basedata.importType')" min-width="200">
            <template v-slot="scope">
              {{ scope.row.templateType }}
            </template>
          </el-table-column>
          <el-table-column align="center" :label="$t('basedata.importData')" min-width="180">
            <template v-slot="scope">
              {{ formatDate({ date: scope.row.createDateTime, formatStr: 'yyyy-MM-dd HH:mm:ss' }) }}
            </template>
          </el-table-column>
          <el-table-column align="center" :label="$t('basedata.importStatus')" min-width="100">
            <template v-slot="scope">
              <span v-if="scope.row.state === '0'" style="color:green;">{{ statusText(scope.row.state) }}</span>
              <span v-if="scope.row.state === '1'" style="color:red;">{{ statusText(scope.row.state) }}</span>
            </template>
          </el-table-column>
          <el-table-column align="center" :label="$t('common.operat')" min-width="100">
            <template v-slot="scope">
              <el-button type="text" size="mini" @click="downloadFile(scope.row.id)"> {{ $t('basedata.download') }} </el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>
      <div class="page">
        <el-pagination
          background
          :current-page="importHisForm.currentPage"
          :page-sizes="[10, 20, 30, 40]"
          :page-size="importHisForm.pagesize"
          layout="total, sizes, prev, pager, next"
          :total="listSize"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
      <el-dialog
        :visible.sync="dialogVisible"
        :title="$t('basedata.importXLS')"
        width="30%"
      >
        <el-form label-width="25%">
          <el-form-item :label="$t('basedata.selectFile')">
            <div>
              <el-input v-model="fileName" disabled size="mini" style="width: 80%; display: inline-block;" /> &nbsp;&nbsp;
              <el-button size="mini" @click="fileSelect()">...</el-button>
              <input ref="fileagain" type="file" style="display: none;" class="upfile" name="file" accept=".xls" @change="changeInput()">
            </div>
          </el-form-item>
        </el-form>
        <div slot="footer" style="text-align:right">
          <el-button type="primary" size="mini" @click="uploadFile('0')">{{ $t('common.btnConfirm') }}</el-button>
          <el-button size="mini" @click="dialogVisible = false">{{ $t('common.btnCancel') }}</el-button>
        </div>
      </el-dialog>
    </div>
  </div>
</template>

<script>
import { getTBasedataImportHis, getTemplateType, outPutXLS, importXLS, outPutFile } from '@/api/basedata.js'
import { formatDate } from '@/utils/common.js'
export default {
  data() {
    return {
      value: '',
      options: [],
      list: [],
      listSize: 0,
      tableHeight: 0,
      listLoading: true,
      importHisForm: {
        templateType: '',
        currentPage: 1,
        pagesize: 20
      },
      dialogVisible: false,
      fileName: '',
      btnDisabled: false
    }
  },
  created() {
    const screenHeight = document.documentElement.clientHeight - 200 + 'px'
    this.$nextTick(() => {
      this.tableHeight = screenHeight
    })
  },
  mounted() {
    this.getTemplateTypeData()
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
      return (~~this.importHisForm.currentPage - 1) * this.importHisForm.pagesize + index + 1
    },
    statusText(data) {
      if (data === '0') {
        return this.$t('basedata.success')
      }
      if (data === '1') {
        return this.$t('basedata.failure')
      }
    },
    // 获取基础数据导入履历
    selectChange() {
      this.getTBasedataImportHisData()
      // 重置翻页组件数据
      this.importHisForm.currentPage = 1
      this.importHisForm.pagesize = 20
    },

    getTBasedataImportHisData() {
      // 调用获取基础数据导入履历接口
      this.listLoading = true
      this.importHisForm.templateType = this.value
      getTBasedataImportHis(this.importHisForm).then(res => {
        if (res.data && res.code === 0) {
          this.list = res.data.records
          this.listSize = res.data.total
        }
      }).finally(() => {
        this.listLoading = false
      })
    },

    getTemplateTypeData() {
      this.listLoading = true
      getTemplateType().then(res => {
        if (res.data && res.code === 0) {
          this.options = res.data
          this.value = res.data[0].value
          this.getTBasedataImportHisData()
        }
      }).finally(() => {
        this.listLoading = false
      })
    },

    // 文件
    uploadFile(flg) {
      // 是否选中文件
      if (this.$refs.fileagain.files.length === 0) {
        this.$message({
          type: 'warning',
          message: this.$t('basedata.fileNull'),
          duration: 1000
          // showClose: true
        })
        return
      }
      let text = ''
      if (flg === '0') {
        text = this.$t('basedata.uplodeConfirm')
      } else {
        text = this.$t('basedata.mandatoryImport')
      }
      // 是否上传？
      this.$confirm(text, this.$t('common.prompt'), {
        // 确认
        confirmButtonText: this.$t('common.btnConfirm'),
        // 取消
        cancelButtonText: this.$t('common.btnCancel'),
        cancelButtonClass: 'bhh-btn-custom-cancel'
      }).then(async() => {
        this.listLoading = true
        const formData = new FormData()
        formData.append('file', this.$refs.fileagain.files[0])
        formData.append('fileName', this.fileName)
        formData.append('modelFlg', flg)
        formData.append('templateType', this.importHisForm.templateType)
        importXLS(formData).then(res => {
          if (res.code === 0) {
            this.selectChange()
            // 重置翻页组件数据
            this.importHisForm.currentPage = 1
            this.importHisForm.pagesize = 20
            if (res.isSuccess === '1') {
              this.$alert(res.data, this.$t('basedata.importFailure'), {
                confirmButtonText: this.$t('common.btnConfirm'),
                callback: action => {
                }
              })
            } else if (res.isSuccess === '2') {
              this.$confirm(res.data, this.$t('basedata.importFailure'), {
                confirmButtonText: this.$t('common.btnConfirm'),
                cancelButtonText: this.$t('common.btnCancel'),
                cancelButtonClass: 'bhh-btn-custom-cancel'
              }).then(async() => {
                this.uploadFile('1')
              })
            } else {
              this.$alert(this.$t('basedata.uplodeSuccess'), this.$t('basedata.importSuccess'), {
                confirmButtonText: this.$t('common.btnConfirm'),
                callback: action => {
                }
              })
            }
          }
        }).finally(() => {
          this.listLoading = false
          this.dialogVisible = false
        })
      })
    },

    downloadTemplate() {
      this.listLoading = true
      this.btnDisabled = true
      outPutXLS(this.importHisForm).then(res => {
        if (res !== undefined) {
          const temp = localStorage.getItem('content-disposition').split(';')[1].split('filename=')[1]
          let filename = decodeURIComponent(temp)
          filename = filename.replace(/\"/g, '')
          const blob = new Blob([res], { type: 'application/vnd.ms-excel;charset=UTF-8' })
          const a = document.createElement('a')
          a.download = filename
          a.href = window.URL.createObjectURL(blob)
          a.click()
          window.URL.revokeObjectURL(a.href) // 释放URL 对象
        }
      }).finally(() => {
        this.listLoading = false
        this.btnDisabled = false
      })
    },

    downloadFile(id) {
      this.listLoading = true
      outPutFile(id).then(res => {
        if (res !== undefined) {
          const temp = localStorage.getItem('content-disposition').split(';')[1].split('filename=')[1]
          let filename = decodeURIComponent(temp)
          filename = filename.replace(/\"/g, '')
          const blob = new Blob([res], { type: 'application/vnd.ms-excel;charset=UTF-8' })
          const a = document.createElement('a')
          a.download = filename
          a.href = window.URL.createObjectURL(blob)
          a.click()
          window.URL.revokeObjectURL(a.href) // 释放URL 对象
        }
      }).finally(() => {
        this.listLoading = false
      })
    },

    // 打开文件上传页面
    showDialogVisible() {
      this.fileName = ''
      if (this.$refs.fileagain !== undefined) {
        this.$refs.fileagain.value = ''
      }
      this.dialogVisible = true
    },
    // 修改每页展示多少条数据
    handleSizeChange(size) {
      this.importHisForm.pagesize = size
      this.getTBasedataImportHisData()
    },
    // 翻页
    handleCurrentChange(page) {
      this.importHisForm.currentPage = page
      this.getTBasedataImportHisData()
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
    formatDate(params) {
      return formatDate(params)
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
