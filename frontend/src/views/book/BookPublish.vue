<template>
  <div class="book-publish-page">
    <div class="container">
      <n-card class="publish-card">
        <template #header>
          <div class="card-header">
            <n-icon size="28" color="#18a058"><CreateOutline /></n-icon>
            <div>
              <h2>{{ isEdit ? '编辑书籍' : '发布书籍' }}</h2>
              <p>{{ isEdit ? '修改您的书籍信息' : '填写书籍信息，让更多人发现它' }}</p>
            </div>
          </div>
        </template>
        
        <n-form ref="formRef" :model="form" :rules="rules" label-placement="left" label-width="100"
                require-mark-placement="right-hanging">
          <n-grid :cols="24" :x-gap="24">
            <n-form-item-gi :span="24" label="书名" path="title">
              <n-input v-model:value="form.title" placeholder="请输入书名" maxlength="200" show-count />
            </n-form-item-gi>
            
            <n-form-item-gi :span="12" label="作者" path="author">
              <n-input v-model:value="form.author" placeholder="请输入作者" maxlength="100" />
            </n-form-item-gi>
            
            <n-form-item-gi :span="12" label="分类" path="categoryId">
              <n-select v-model:value="form.categoryId" :options="categoryOptions" placeholder="请选择分类" />
            </n-form-item-gi>
            
            <n-form-item-gi :span="12" label="售价" path="price">
              <n-input-number v-model:value="form.price" :min="0" :precision="2" placeholder="请输入售价"
                              style="width: 100%">
                <template #prefix>¥</template>
              </n-input-number>
            </n-form-item-gi>
            
            <n-form-item-gi :span="12" label="原价">
              <n-input-number v-model:value="form.originalPrice" :min="0" :precision="2" 
                              placeholder="选填，用于展示折扣" style="width: 100%">
                <template #prefix>¥</template>
              </n-input-number>
            </n-form-item-gi>
            
            <n-form-item-gi :span="24" label="成色" path="conditionLevel">
              <n-radio-group v-model:value="form.conditionLevel" name="condition">
                <n-space>
                  <n-radio :value="1">
                    <div class="condition-option">
                      <span class="condition-label">全新</span>
                      <span class="condition-desc">未拆封或仅翻阅</span>
                    </div>
                  </n-radio>
                  <n-radio :value="2">
                    <div class="condition-option">
                      <span class="condition-label">九成新</span>
                      <span class="condition-desc">轻微使用痕迹</span>
                    </div>
                  </n-radio>
                  <n-radio :value="3">
                    <div class="condition-option">
                      <span class="condition-label">八成新</span>
                      <span class="condition-desc">有使用痕迹</span>
                    </div>
                  </n-radio>
                  <n-radio :value="4">
                    <div class="condition-option">
                      <span class="condition-label">七成新</span>
                      <span class="condition-desc">明显使用痕迹</span>
                    </div>
                  </n-radio>
                  <n-radio :value="5">
                    <div class="condition-option">
                      <span class="condition-label">六成新以下</span>
                      <span class="condition-desc">较旧但可正常阅读</span>
                    </div>
                  </n-radio>
                </n-space>
              </n-radio-group>
            </n-form-item-gi>
            
            <n-form-item-gi :span="24" label="图片">
              <div class="image-input-wrapper">
                <n-input v-model:value="form.images" type="textarea" placeholder="请输入图片URL，多个用逗号分隔"
                         :autosize="{ minRows: 2, maxRows: 4 }" />
                <p class="input-tip">
                  <n-icon><InformationCircleOutline /></n-icon>
                  支持多张图片，用英文逗号分隔URL。建议上传清晰的书籍封面和内页照片。
                </p>
              </div>
            </n-form-item-gi>
            
            <n-form-item-gi :span="24" label="描述" path="description">
              <n-input v-model:value="form.description" type="textarea" 
                       placeholder="请描述书籍的详细信息，如购买时间、使用情况、是否有笔记等"
                       :autosize="{ minRows: 4, maxRows: 8 }" maxlength="2000" show-count />
            </n-form-item-gi>
          </n-grid>
          
          <div class="form-actions">
            <n-button size="large" @click="router.back()">取消</n-button>
            <n-button type="primary" size="large" :loading="loading" @click="handleSubmit">
              <template #icon><n-icon><CheckmarkOutline /></n-icon></template>
              {{ isEdit ? '保存修改' : '发布书籍' }}
            </n-button>
          </div>
        </n-form>
      </n-card>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useMessage, FormInst, FormRules } from 'naive-ui'
import { CreateOutline, CheckmarkOutline, InformationCircleOutline } from '@vicons/ionicons5'
import { bookApi } from '@/service/book'
import { categoryApi } from '@/service/category'
import type { BookForm, Category } from '@/types'

const router = useRouter()
const route = useRoute()
const message = useMessage()

const isEdit = computed(() => route.name === 'BookEdit')
const bookId = computed(() => Number(route.params.id))

const loading = ref(false)
const formRef = ref<FormInst | null>(null)
const categories = ref<Category[]>([])

const form = ref<BookForm & { images: string }>({
  title: '', author: '', price: 0, originalPrice: undefined,
  conditionLevel: 2, description: '', images: '', categoryId: 0
})

const categoryOptions = computed(() =>
  categories.value.map(c => ({ label: c.name, value: c.id }))
)

const rules: FormRules = {
  title: { required: true, message: '请输入书名', trigger: 'blur' },
  author: { required: true, message: '请输入作者', trigger: 'blur' },
  categoryId: { required: true, type: 'number', message: '请选择分类', trigger: 'change' },
  price: { required: true, type: 'number', min: 0.01, message: '请输入有效价格', trigger: 'blur' },
  conditionLevel: { required: true, type: 'number', message: '请选择成色', trigger: 'change' }
}

async function fetchCategories() {
  try { categories.value = (await categoryApi.list()).data } catch {}
}

async function fetchBook() {
  if (!isEdit.value || !bookId.value) return
  try {
    const book = (await bookApi.getById(bookId.value)).data
    form.value = {
      title: book.title, author: book.author, price: book.price,
      originalPrice: book.originalPrice, conditionLevel: book.conditionLevel,
      description: book.description || '', images: book.images || '', categoryId: book.categoryId
    }
  } catch { message.error('获取书籍信息失败'); router.back() }
}

async function handleSubmit() {
  try { await formRef.value?.validate() } catch { return }
  
  loading.value = true
  try {
    const data: BookForm = {
      ...form.value,
      images: form.value.images ? JSON.stringify(form.value.images.split(',').map(s => s.trim())) : undefined
    }
    
    if (isEdit.value) {
      await bookApi.update(bookId.value, data)
      message.success('修改成功')
    } else {
      await bookApi.create(data)
      message.success('发布成功')
    }
    router.push('/home/my/books')
  } catch {} finally { loading.value = false }
}

onMounted(() => { fetchCategories(); fetchBook() })
</script>

<style scoped>
.book-publish-page { padding: 24px 0; background: #f8f9fa; min-height: calc(100vh - 120px); }

.container { max-width: 900px; margin: 0 auto; padding: 0 16px; }

.publish-card { border-radius: 16px; }

.card-header {
  display: flex;
  align-items: center;
  gap: 16px;
}

.card-header h2 { margin: 0 0 4px; font-size: 20px; font-weight: 600; }
.card-header p { margin: 0; color: #666; font-size: 14px; }

.condition-option {
  display: flex;
  flex-direction: column;
  padding: 8px 0;
}

.condition-label { font-weight: 500; color: #1a1a1a; }
.condition-desc { font-size: 12px; color: #999; margin-top: 2px; }

.image-input-wrapper { width: 100%; }

.input-tip {
  display: flex;
  align-items: center;
  gap: 6px;
  margin: 8px 0 0;
  font-size: 12px;
  color: #999;
}

.form-actions {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  margin-top: 32px;
  padding-top: 24px;
  border-top: 1px solid #f0f0f0;
}

@media (max-width: 768px) {
  .form-actions { flex-direction: column; }
  .form-actions .n-button { width: 100%; }
}
</style>
