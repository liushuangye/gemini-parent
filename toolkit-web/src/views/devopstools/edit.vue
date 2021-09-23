<template>
  <div class="app-container" style="font-size: 15px;">
    <div>
      <span>{{ $t('devopstools.toolName') }} :</span>
      <el-input v-model="devopsName" size="mini" style="width:20%;margin-left: 10px;" />
    </div>
    <div style="margin-top: 10px;">
      <span style="vertical-align:top;">{{ $t('devopstools.featuresDesc') }} :</span>
      <el-input v-model="deptdesc" type="textarea" size="mini" :rows="3" style="width:40%;margin-left: 10px;" />
    </div>
    <div style="margin-top: 10px;">
      <span>{{ $t('devopstools.selectFile') }} :</span>
      <el-input v-model="fileName" size="mini" disabled style="width:40%;margin-left: 10px;display: inline-block;" />
      <el-button size="mini" style="margin-left: 10px;" @click="fileSelect()">...</el-button>
      <input ref="fileagain" type="file" style="display: none;" class="upfile" name="file" accept=".xml" @change="changeInput()">
    </div>
    <div v-for="(item,index) in step" :key="index" style="margin-top: 20px;">
      <el-card style="width: 90%;">
        <div>
          {{ $t('devopstools.step') }}{{ item.id }}:
          {{ item.name }}
        </div>
        <div style="margin-top: 20px;margin-left: 40px;">
          {{ $t('devopstools.description') }}:
          {{ item.desc }}
        </div>
        <div style="margin-top: 20px;margin-left: 40px;">
          {{ $t('devopstools.condition') }}:
          <span v-if="item.paramsOfWhere != null" :span="20">
            <span v-for="(ment,num) in item.paramsOfWhere.param" :key="num">
              {{ ment.value }}: <el-input disabled :placeholder="ment.placeholder" size="mini" style="width:17%;margin-left: 5px;" />
            </span>
          </span>
        </div>
        <div v-if="item.type==='QUERY'" style="margin-top: 20px;margin-left: 40px;">
          {{ $t('devopstools.result') }}:
        </div>
        <div v-if="item.type==='UPDATE'" style="margin-top: 20px;margin-left: 40px;">
          {{ $t('devopstools.updated') }}:
          <span v-if="item.paramsOfSet != null">
            <span v-for="(ment,num) in item.paramsOfSet.param" :key="num">
              {{ ment.value }}: <el-input disabled :placeholder="ment.placeholder" size="mini" style="width:17%;margin-left: 5px;" />
            </span>
          </span>
        </div>
      </el-card>
    </div>
    <el-button type="primary" style="margin-left: 40%;margin-top: 20px;" @click="updateTemplateDate()">{{ $t('devopstools.save') }}</el-button>
    <el-button style="margin-left: 20px;margin-top: 20px;" @click="btnReturn()">{{ $t('common.btnReturn') }}</el-button>
  </div>
</template>
<script>
import { getById, updateTemplate } from '@/api/devops.js'
export default {
  data() {
    return {
      devopsName: '',
      deptdesc: '',
      step: [],
      fileName: ''
    }
  },
  mounted() {
    this.getTool()
  },
  methods: {
    getTool() {
      getById(this.$route.params.devopsId).then(res => {
        if (res.data && res.code === 0) {
          this.devopsName = res.data.devopsName
          this.deptdesc = res.data.deptdesc
          this.step = res.data.devops.steps.step
        }
      })
    },
    btnReturn() {
      this.$router.push({ path: '/devopstools/index' })
    },
    updateTemplateDate() {
      // 工具名称 check
      if (this.devopsName === '') {
        this.$message({
          type: 'warning',
          message: this.$t('devopstools.toolNameNull'),
          duration: 1000
        })
        return
      }
      // 是否上传？
      this.$confirm(this.$t('devopstools.updateConfirm'), this.$t('common.prompt'), {
        // 确认
        confirmButtonText: this.$t('common.btnConfirm'),
        // 取消
        cancelButtonText: this.$t('common.btnCancel'),
        cancelButtonClass: 'bhh-btn-custom-cancel'
      }).then(async() => {
        const formData = new FormData()
        formData.append('id', this.$route.params.devopsId)
        formData.append('devopsName', this.devopsName)
        formData.append('deptdesc', this.deptdesc)
        // 是否选中文件
        if (this.$refs.fileagain.files.length > 0) {
          formData.append('xml', this.$refs.fileagain.files[0])
        }
        formData.append('fileName', this.fileName)
        updateTemplate(formData).then(res => {
          if (res.code === 0) {
            this.$message({
              type: 'success',
              message: this.$t('devopstools.updateSuccess'),
              duration: 1000
            })
          }
        }).finally(() => {
          this.btnReturn()
        })
      })
    },
    // 打开选择文件页面
    fileSelect() {
      this.$refs.fileagain.click()
    },
    // 选择文件显示文件名
    changeInput() {
      const file = this.$refs.fileagain.files[0]
      this.fileName = file.name
    }
  }
}
</script>

<style scoped>
.div-a{ float:left;width:90%;}
.div-b{ float:left;width:10%;}
</style>
