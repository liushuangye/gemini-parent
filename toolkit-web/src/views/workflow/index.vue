<template>
  <div class="usernamage">
    <router-view />
    <div class="usernamage-wrap">
      <div class="search">
        <el-form :inline="true">
          <el-form-item :label="$t('workflow.modelName')">
            <el-input v-model.trim="modelForm.modelName" size="mini" class="inputWidth" />
          </el-form-item>
          <!--列表页按钮 -->
          <el-form-item label-width="1px">
            <el-button type="primary" size="mini" @click="selectList()">{{ $t('common.btnFind') }}</el-button>
            <el-button type="primary" size="mini" @click="resetList()">{{ $t('common.btnReset') }}</el-button>
            <el-button type="primary" size="mini" @click="createBpmnModel()">{{ $t('workflow.create') }}</el-button>
            <el-button type="primary" size="mini" @click="showDialogVisible()">{{ $t('workflow.import') }}</el-button>
            <el-button type="primary" size="mini" @click="exportSelect()">{{ $t('workflow.export') }}</el-button>
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
          @selection-change="handleSelectionChange"
        >
          <el-table-column
            type="selection"
            width="40">
          </el-table-column>
          <el-table-column type="index" align="center" :index="indexMethod" :label="$t('workflow.num')" width="50" />
          <el-table-column align="center" :label="$t('workflow.model.id')" min-width="150">
            <template v-slot="scope">
              {{ scope.row.id }}
            </template>
          </el-table-column>
          <!-- Key在xml里面，不好拿,约定name存key
          <el-table-column align="center" :label="$t('workflow.model.key')" min-width="150">
            <template v-slot="scope">
              {{ scope.row.key }}
            </template>
          </el-table-column>
          -->
          <el-table-column align="center" :label="$t('workflow.model.name')" min-width="150">
            <template v-slot="scope">
              {{ scope.row.name }}
            </template>
          </el-table-column>
          <el-table-column align="center" :label="$t('workflow.model.version')" min-width="150">
            <template v-slot="scope">
              {{ scope.row.version }}
            </template>
          </el-table-column>
          <el-table-column align="center" :label="$t('workflow.model.description')" min-width="200">
            <template v-slot="scope">
              {{ scope.row.description }}
            </template>
          </el-table-column>
          <el-table-column align="center" :label="$t('workflow.model.createTime')" min-width="150">
            <template v-slot="scope">
              {{ scope.row.createTime }}
            </template>
          </el-table-column>
          <el-table-column align="center" :label="$t('workflow.model.lastUpdateTime')" min-width="150">
            <template v-slot="scope">
              {{ scope.row.lastUpdateTime }}
            </template>
          </el-table-column>
          <el-table-column align="center" :label="$t('workflow.model.deploymentId')" min-width="150">
            <template v-slot="scope">
              {{ scope.row.deploymentId }}
            </template>
          </el-table-column>
          <el-table-column align="center" fixed="right" :label="$t('common.operat')" min-width="150">
            <template v-slot="scope">
              <el-button type="text" style="margin-left: 10px;" size="mini" @click="design(scope.row.id)"> {{ $t('workflow.design') }} </el-button>
              <el-button type="text" style="margin-left: 10px;" size="mini" @click="deploy(scope.row.id)"> {{ $t('workflow.deploy') }} </el-button>
              <el-button type="text" style="margin-left: 10px;color:#F00;" size="mini" @click="deleteRow(scope.row.id)"> {{ $t('common.delete') }} </el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>
      <div class="page">
        <el-pagination
          background
          :current-page="modelForm.pageNum"
          :page-sizes="[10, 20, 30, 40]"
          :page-size="modelForm.pageSize"
          layout="total, sizes, prev, pager, next"
          :total="listSize"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
      <el-dialog
        :visible.sync="dialogVisible"
        :title="$t('workflow.importBpmnModel')"
        width="30%"
      >
        <el-form label-width="25%">
          <el-form-item :label="$t('workflow.modelName')">
            <el-input v-model="modelName" size="mini" style="width: 80%;" />
          </el-form-item>
          <el-form-item :label="$t('workflow.description')">
            <el-input v-model="description" size="mini" type="textarea" :rows="3" style="width: 80%;" />
          </el-form-item>
          <el-form-item :label="$t('workflow.selectFile')">
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
import { listModel, create, design , deploy , importBpmnModel , exportSelect , deleteModel } from '@/api/workflow.js'
export default {
  data() {
    return {
      multipleSelection: [],
      listLoading: true,
      list: [],
      tableHeight: 0,
      listSize: 0,
      modelForm: {
        modelName: '',
        pageNum: 1,
        pageSize: 20
      },
      dialogVisible: false,
      fileName: '',
      modelName: '',
      description: '',
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
      return (~~this.modelForm.pageNum - 1) * this.modelForm.pageSize + index + 1
    },
    // table选中数据
    handleSelectionChange(val) {
      this.multipleSelection = val
    },
    // 页面打开
    selectList() {
      this.listLoading = true
      listModel(this.modelForm).then(res => {
        if (res.data && res.code == '200') {
          console.log(res.data)
          this.list = res.data.records
          this.listSize = res.data.total
        }
      }).finally(() => {
        this.listLoading = false
      })
    },
    // 重置
    resetList() {
      this.modelForm.modelName = ''
      // 重置翻页组件数据
      this.modelForm.pageNum = 1
      this.modelForm.pageSize = 20
      this.selectList()
    },
    // 删除模型
    deleteRow(data) {
      // 是否删除？
      this.$confirm(this.$t('common.delConfirm'), this.$t('common.prompt'), {
        // 确认
        confirmButtonText: this.$t('common.btnConfirm'),
        // 取消
        cancelButtonText: this.$t('common.btnCancel'),
        cancelButtonClass: 'bhh-btn-custom-cancel'
      }).then(async() => {
        this.listLoading = true
        deleteModel(data).then(res => {
          // 重置翻页组件数据
          this.modelForm.pageNum = 1
          this.modelForm.pageSize = 20
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
      this.modelForm.pageSize = size
      this.selectList()
    },
    // 翻页
    handleCurrentChange(page) {
      this.modelForm.pageNum = page
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
      // 模型名称 check
      if (this.modelName === '') {
        this.$message({
          type: 'warning',
          message: this.$t('workflow.modelNameNull'),
          duration: 1000
        })
        return
      }

      // 是否选中文件
      if (this.$refs.fileagain.files.length === 0) {
        this.$message({
          type: 'warning',
          message: this.$t('workflow.selectNull'),
          duration: 1000
        })
        return
      }

      // 是否上传？
      this.$confirm(this.$t('workflow.uplodeConfirm'), this.$t('common.prompt'), {
        // 确认
        confirmButtonText: this.$t('common.btnConfirm'),
        // 取消
        cancelButtonText: this.$t('common.btnCancel'),
        cancelButtonClass: 'bhh-btn-custom-cancel'
      }).then(async() => {
        this.listLoading = true
        const formData = new FormData()
        formData.append('modelName', this.modelName)
        formData.append('description', this.description)
        formData.append('file', this.$refs.fileagain.files[0])
        formData.append('fileName', this.fileName)
        // 导入流程模型
        importBpmnModel(formData).then(res => {

          if (res.code == 200) {
            // 重置翻页组件数据
            this.modelForm.pageNum = 1
            this.modelForm.pageSize = 20
            this.selectList()
            this.$message({
              type: 'success',
              message: this.$t('workflow.uplodeSuccess'),
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
      this.modelName = ''
      this.fileName = ''
      if (this.$refs.fileagain !== undefined) {
        this.$refs.fileagain.value = ''
      }
      this.dialogVisible = true
    },
    // 打开activiti设计器：新建
    createBpmnModel() {
      create();
    },
    //打开activiti设计器：编辑
    design(modelId) {
      design(modelId)
    },
    //发布模型
    deploy(modelId) {
      const data = {"modelId":modelId}
      deploy(data).then(res => {
        if (res.code == 200) {
          this.selectList()
          this.$message({
            type: 'success',
            message: this.$t('workflow.deploySuccess'),
            duration: 1000
          })
        }
      })
    },
    //导出模型
    exportSelect() {
      exportSelect(this.multipleSelection).then(res => {
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
      })
    },
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
